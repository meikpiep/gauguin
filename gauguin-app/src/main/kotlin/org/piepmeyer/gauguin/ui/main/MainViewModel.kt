package org.piepmeyer.gauguin.ui.main

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.calculation.GridCalculationState
import org.piepmeyer.gauguin.game.FastFinishingModeState
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameSolvedListener
import org.piepmeyer.gauguin.game.NishioCheckState
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

enum class GameState {
    PLAYING,
    CALCULATING_NEW_GRID,
    SOLVED,
    ALREADY_SOLVED,
}

data class GameStateWithGrid(
    val state: GameState,
    val grid: Grid,
)

private val logger = KotlinLogging.logger {}

class MainViewModel(
    applicationScope: CoroutineScope,
) : KoinComponent,
    GameSolvedListener {
    private val calculationService: GridCalculationService by inject()
    private val game: Game by inject()
    private val preferences: ApplicationPreferences by inject()

    private val mutableGameStateWithGrid = MutableStateFlow(calculateGameStateWithGridFromCurrentGrid())
    private val mutableKeepScreenOnState =
        MutableStateFlow(mutableGameStateWithGrid.value.state == GameState.PLAYING && preferences.keepScreenOn())

    val gameStateWithGrid: StateFlow<GameStateWithGrid> = mutableGameStateWithGrid.asStateFlow()
    val fastFinishingModeState: StateFlow<FastFinishingModeState> = game.fastFinishingModeState
    val keepScreenOnState: StateFlow<Boolean> = mutableKeepScreenOnState.asStateFlow()

    val nextGridState: StateFlow<GridCalculationState>
    val nishioCheckState: StateFlow<NishioCheckState>

    init {
        applicationScope.launch {
            calculationService.currentGridState.collect {
                logger.debug { "Current grid calculation state: $it" }

                when (it) {
                    GridCalculationState.CALCULATED -> {
                        mutableGameStateWithGrid.value = calculateGameStateWithGridFromCurrentGrid()
                    }

                    GridCalculationState.CURRENTLY_CALCULATING -> {
                        mutableGameStateWithGrid.value = GameStateWithGrid(GameState.CALCULATING_NEW_GRID, game.grid)
                    }
                }

                updateKeepScreenOn()
            }
        }

        nextGridState = calculationService.nextGridState
        nishioCheckState = game.nishioCheckState

        applicationScope.launch {
            game.gridState.collect {
                mutableGameStateWithGrid.value = calculateGameStateWithGridFromCurrentGrid()
                updateKeepScreenOn()
            }
        }

        game.addGameSolvedHandler(this)
    }

    private fun updateKeepScreenOn() {
        mutableKeepScreenOnState.value = mutableGameStateWithGrid.value.state == GameState.PLAYING && preferences.keepScreenOn()
    }

    private fun calculateGameStateWithGridFromCurrentGrid() =
        GameStateWithGrid(
            if (game.grid.isSolved()) {
                GameState.ALREADY_SOLVED
            } else {
                GameState.PLAYING
            },
            game.grid,
        )

    override fun puzzleSolved() {
        mutableGameStateWithGrid.value = GameStateWithGrid(GameState.SOLVED, game.grid)
        updateKeepScreenOn()
    }

    fun restartedGame() {
        game.restartGame()

        mutableGameStateWithGrid.value = GameStateWithGrid(GameState.PLAYING, game.grid)
        updateKeepScreenOn()
    }
}
