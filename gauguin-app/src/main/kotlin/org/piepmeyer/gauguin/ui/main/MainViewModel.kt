package org.piepmeyer.gauguin.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.calculation.GridCalculationListener
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.calculation.NextGridState
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameModeListener
import org.piepmeyer.gauguin.game.GameSolvedListener
import org.piepmeyer.gauguin.game.GridCreationListener
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

enum class FastFinishingModeState {
    ACTIVE,
    INACTIVE,
}

class MainViewModel :
    ViewModel(),
    KoinComponent,
    GridCreationListener,
    GameSolvedListener,
    GameModeListener {
    private val calculationService: GridCalculationService by inject()
    private val game: Game by inject()
    private val preferences: ApplicationPreferences by inject()

    private val mutableGameStateWithGrid = MutableStateFlow(initialUiState())
    private val mutableFastFinishingModeState = MutableStateFlow(FastFinishingModeState.INACTIVE)
    private val mutableKeepScreenOnState =
        MutableStateFlow(mutableGameStateWithGrid.value.state == GameState.PLAYING && preferences.keepScreenOn())

    val gameStateWithGrid: StateFlow<GameStateWithGrid> = mutableGameStateWithGrid.asStateFlow()
    val fastFinishingModeState: StateFlow<FastFinishingModeState> = mutableFastFinishingModeState.asStateFlow()
    val keepScreenOnState: StateFlow<Boolean> = mutableKeepScreenOnState.asStateFlow()

    val nextGridState: StateFlow<NextGridState>

    init {
        calculationService.addListener(createGridCalculationListener())

        nextGridState = calculationService.nextGridState

        game.addGridCreationListener(this)
        game.addGameSolvedHandler(this)
        game.addGameModeListener(this)
    }

    override fun onCleared() {
        game.removeGridCreationListener(this)
        game.removeGameSolvedHandler(this)
        game.removeGameModeListener(this)
    }

    private fun createGridCalculationListener(): GridCalculationListener =
        object : GridCalculationListener {
            override fun startingCurrentGridCalculation() {
                mutableGameStateWithGrid.value = GameStateWithGrid(GameState.CALCULATING_NEW_GRID, game.grid)
                updateKeepScreenOn()
            }

            override fun currentGridCalculated() {
                mutableGameStateWithGrid.value = GameStateWithGrid(GameState.PLAYING, game.grid)
                updateKeepScreenOn()
            }
        }

    private fun updateKeepScreenOn() {
        mutableKeepScreenOnState.value = mutableGameStateWithGrid.value.state == GameState.PLAYING && preferences.keepScreenOn()
    }

    override fun freshGridWasCreated() {
        mutableGameStateWithGrid.value = GameStateWithGrid(GameState.PLAYING, game.grid)
        updateKeepScreenOn()
    }

    private fun initialUiState() =
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

    override fun changedGameMode() {
        mutableFastFinishingModeState.value =
            if (game.isInFastFinishingMode()) {
                FastFinishingModeState.ACTIVE
            } else {
                FastFinishingModeState.INACTIVE
            }
    }
}
