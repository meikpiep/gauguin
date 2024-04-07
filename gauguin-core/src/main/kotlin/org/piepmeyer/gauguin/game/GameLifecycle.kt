package org.piepmeyer.gauguin.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.koin.core.annotation.InjectedParam
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.game.save.SaveGame
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.preferences.StatisticsManager
import java.io.File
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

@Suppress("EmptyMethod")
class GameLifecycle(
    private var saveGameDirectory: File,
    @InjectedParam private val game: Game,
    @InjectedParam private val applicationPreferences: ApplicationPreferences,
    @InjectedParam private val calculationService: GridCalculationService,
    @InjectedParam private val statisticsManager: StatisticsManager,
) {
    private lateinit var scope: CoroutineScope
    private var playTimerThreadContext: CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private var starttime: Long = 0
    private var playTimeListeners = mutableListOf<PlayTimeListener>()
    private var deferredTimer: Deferred<Unit>? = null

    fun addPlayTimeListener(listener: PlayTimeListener) {
        playTimeListeners += listener
    }

    fun removePlayTimeListener(listener: PlayTimeListener) {
        playTimeListeners -= listener
    }

    fun gameSolved() {
        stopGameTimer()
        game.grid.playTime = (System.currentTimeMillis() - starttime).milliseconds

        informPlayTimeListeners()
    }

    private fun gameWasLoaded() {
        starttime = System.currentTimeMillis() - game.grid.playTime.inWholeMilliseconds
        startGameTimer()
    }

    fun pauseGame() {
        stopGameTimer()

        if (game.grid.isActive) {
            game.grid.playTime = (System.currentTimeMillis() - starttime).milliseconds
        }

        val saver = SaveGame.autosaveByDirectory(this.saveGameDirectory)
        saver.save(game.grid)
    }

    fun setCoroutineScope(scope: CoroutineScope) {
        this.scope = scope
    }

    fun resumeGame() {
        gameWasLoaded()
    }

    fun startNewGrid() {
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

    fun showGrid() {
        game.updateGrid(game.grid)
    }

    private fun startGameTimer() {
        stopGameTimer()

        deferredTimer =
            scope.launchGameTimer {
                game.grid.playTime = (System.currentTimeMillis() - starttime).milliseconds

                informPlayTimeListeners()
            }
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

    fun postNewGame(
        startedFromMainActivityWithSameVariant: Boolean,
        lifecycleScope: CoroutineScope,
    ) {
        if (game.grid.isActive && game.grid.startedToBePlayed) {
            statisticsManager.storeStreak(false)
        }

        val variant =
            if (startedFromMainActivityWithSameVariant) {
                game.grid.variant
            } else {
                GameVariant(
                    GridSize(
                        applicationPreferences.gridWidth,
                        applicationPreferences.gridHeigth,
                    ),
                    applicationPreferences.gameVariant,
                )
            }

        if (calculationService.hasCalculatedNextGrid(variant)) {
            val grid = calculationService.consumeNextGrid()
            grid.isActive = true
            game.updateGrid(grid)

            calculationService.calculateNextGrid(lifecycleScope)
        } else {
            calculationService.calculateCurrentAndNextGrids(variant, lifecycleScope)
        }
    }

    fun loadGame(saveGameFile: File) {
        val saver = SaveGame.createWithFile(saveGameFile)

        saver.restore()?.let {
            game.updateGrid(it)
            gameWasLoaded()
        }
    }
}
