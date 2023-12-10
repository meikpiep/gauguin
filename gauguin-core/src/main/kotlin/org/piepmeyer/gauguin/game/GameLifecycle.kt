package org.piepmeyer.gauguin.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import java.io.File
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

class GameLifecycle(
    private var saveGameDirectory: File,
) : KoinComponent {
    private val game: Game by inject()
    private val applicationPreferences: ApplicationPreferences by inject()

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

        informListeners()
    }

    fun gameWasLoaded() {
        starttime = System.currentTimeMillis() - game.grid.playTime.inWholeMilliseconds
        startGameTimer()
    }

    fun pauseGame() {
        stopGameTimer()
        game.grid.playTime = (System.currentTimeMillis() - starttime).milliseconds
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
        if (applicationPreferences.addPencilsAtStart()) {
            game.grid.addPossiblesAtNewGame()
        }

        starttime = System.currentTimeMillis()
        startGameTimer()
    }

    fun showGrid() {
        if (!game.grid.isSolved) {
            // startGameTimer()
        }
    }

    private fun startGameTimer() {
        stopGameTimer()

        deferredTimer =
            scope.launchPeriodicAsync(500) {
                game.grid.playTime = (System.currentTimeMillis() - starttime).milliseconds

                informListeners()
            }
    }

    private fun informListeners() {
        playTimeListeners.forEach { it.playTimeUpdated() }
    }

    private fun stopGameTimer() {
        deferredTimer?.cancel()
        deferredTimer = null
    }

    private fun CoroutineScope.launchPeriodicAsync(
        repeatMillis: Long,
        action: () -> Unit,
    ) = this.async(playTimerThreadContext) {
        while (isActive) {
            action()
            delay(repeatMillis)
        }
    }
}
