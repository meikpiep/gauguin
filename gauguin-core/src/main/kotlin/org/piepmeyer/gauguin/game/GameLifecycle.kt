package org.piepmeyer.gauguin.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.InjectedParam
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.game.save.SaveGame
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.preferences.StatisticsManagerWriting
import java.io.File
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

class GameLifecycle(
    private var saveGameDirectory: File,
    private val scope: CoroutineScope,
    @InjectedParam private val game: Game,
    @InjectedParam private val applicationPreferences: ApplicationPreferences,
    @InjectedParam private val calculationService: GridCalculationService,
    @InjectedParam private val statisticsManager: StatisticsManagerWriting,
) {
    private var playTimerThreadContext: CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private var starttime: Long = 0
    private var playTimeListeners = mutableListOf<PlayTimeListener>()
    private var deferredTimer: Deferred<Unit>? = null

    init {
        game.addGameVipSolvedHandler {
            gameSolved()
        }
    }

    fun addPlayTimeListener(listener: PlayTimeListener) {
        playTimeListeners += listener
    }

    fun removePlayTimeListener(listener: PlayTimeListener) {
        playTimeListeners -= listener
    }

    private fun gameSolved() {
        stopGameTimer()
        game.grid.playTime = (System.currentTimeMillis() - starttime).milliseconds

        informPlayTimeListeners()
    }

    private fun gameWasLoaded() {
        if (!game.grid.isSolved()) {
            starttime = System.currentTimeMillis() - game.grid.playTime.inWholeMilliseconds
            startGameTimer()
        }
    }

    fun pauseGame() {
        stopGameTimer()

        if (game.grid.isActive) {
            game.grid.playTime = (System.currentTimeMillis() - starttime).milliseconds
        }
    }

    fun saveCurrentGame() {
        val saver = SaveGame.autosaveByDirectory(this.saveGameDirectory)
        saver.save(game.grid)
    }

    fun resumeGame() {
        gameWasLoaded()
    }

    private fun startNewGrid() {
        calculationService.stopCalculations()

        game.grid.isActive = true
        prepareNewGrid()

        starttime = System.currentTimeMillis()
        startGameTimer()
    }

    fun prepareNewGrid() {
        if (applicationPreferences.addPencilsAtStart()) {
            game.grid.addPossiblesAtNewGame()
        }

        if (applicationPreferences.fillSingleCagesAtStart()) {
            game.fillSingleCagesInNewGrid()
        }
    }

    private fun startGameTimer() {
        stopGameTimer()

        deferredTimer =
            scope.launchGameTimer {
                game.grid.playTime = (System.currentTimeMillis() - starttime).milliseconds

                informPlayTimeListeners()
            }
    }

    fun stoppGameTimerAndResetGameTime() {
        stopGameTimer()

        game.grid.playTime = 0.milliseconds

        informPlayTimeListeners()
    }

    private fun informPlayTimeListeners() {
        playTimeListeners.forEach { it.playTimeUpdated() }
    }

    private fun stopGameTimer() {
        deferredTimer?.cancel()
        deferredTimer = null
    }

    private fun CoroutineScope.launchGameTimer(action: () -> Unit) =
        this.async(playTimerThreadContext) {
            while (isActive) {
                action.invoke()
                delay(500)
            }
        }

    fun postNewCalculatedGame(startedFromMainActivityWithSameVariant: Boolean) {
        val variant = gridVariantForNewGame(startedFromMainActivityWithSameVariant)

        if (calculationService.hasCalculatedNextGrid(variant)) {
            val grid =
                runBlocking {
                    calculationService.consumeNextGrid()
                }
            grid.isActive = true

            game.useGrid(grid)
            startNewGrid()
        } else {
            runBlocking {
                calculationService.calculateCurrentGrid(variant, scope) {
                    game.useGrid(it)
                    startNewGrid()
                }
            }
        }

        calculationService.calculateNextGrid(scope)
    }

    private fun gridVariantForNewGame(startedFromMainActivityWithSameVariant: Boolean): GameVariant =
        if (startedFromMainActivityWithSameVariant) {
            game.grid.variant
        } else {
            GameVariant(
                GridSize(
                    applicationPreferences.gridWidth,
                    applicationPreferences.gridHeigth,
                ),
                applicationPreferences.gameOptionsVariant,
            )
        }

    fun loadGame(saveGameFile: File) {
        val saver = SaveGame.createWithFile(saveGameFile)

        saver.restore()?.let {
            calculationService.stopCalculations()

            game.useGrid(it)
            gameWasLoaded()
        }
    }

    fun startNewGame(grid: Grid) {
        grid.isActive = true

        game.useGrid(grid)

        startNewGrid()
    }

    fun startNewCalculatedGame(
        mayBeCalculatedGrid: Grid?,
        variant: GameVariant,
    ) {
        endCurrentGame()

        if (mayBeCalculatedGrid != null) {
            calculationService.stopCalculations()

            calculationService.variant = variant

            startNewGame(mayBeCalculatedGrid)

            calculationService.calculateNextGrid(scope)
        } else {
            postNewCalculatedGame(startedFromMainActivityWithSameVariant = false)
        }
    }

    fun restartGame() {
        game.useGrid(game.grid)

        startNewGrid()
    }

    fun endCurrentGame() {
        statisticsManager.endCurrentGame(game.grid)
    }
}
