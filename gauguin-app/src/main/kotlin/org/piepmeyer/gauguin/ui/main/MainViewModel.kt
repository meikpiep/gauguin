package org.piepmeyer.gauguin.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.calculation.GridCalculationListener
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameModeListener
import org.piepmeyer.gauguin.game.GameSolvedListener
import org.piepmeyer.gauguin.game.GridCreationListener
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

enum class MainUiState {
    PLAYING,
    CALCULATING_NEW_GRID,
    SOLVED,
    ALREADY_SOLVED,
}

data class MainUiStateWithGrid(
    val state: MainUiState,
    val grid: Grid,
)

enum class NextGridState {
    CALCULATED,
    CURRENTLY_CALCULATING,
}

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

    private val mutableUiState = MutableStateFlow(initialUiState())
    private val mutableNextGridState = MutableStateFlow(NextGridState.CALCULATED)
    private val mutableFastFinishingModeState = MutableStateFlow(FastFinishingModeState.INACTIVE)
    private val mutableKeepScreenOnState = MutableStateFlow(mutableUiState.value.state == MainUiState.PLAYING && preferences.keepScreenOn())

    val uiState: StateFlow<MainUiStateWithGrid> = mutableUiState.asStateFlow()
    val nextGridState: StateFlow<NextGridState> = mutableNextGridState.asStateFlow()
    val fastFinishingModeState: StateFlow<FastFinishingModeState> = mutableFastFinishingModeState.asStateFlow()
    val keepScreenOnState: StateFlow<Boolean> = mutableKeepScreenOnState.asStateFlow()

    init {
        calculationService.addListener(createGridCalculationListener())
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
                mutableUiState.value = MainUiStateWithGrid(MainUiState.CALCULATING_NEW_GRID, game.grid)
                updateKeepScreenOn()
            }

            override fun currentGridCalculated() {
                mutableUiState.value = MainUiStateWithGrid(MainUiState.PLAYING, game.grid)
                updateKeepScreenOn()
            }

            override fun startingNextGridCalculation() {
                mutableNextGridState.value = NextGridState.CURRENTLY_CALCULATING
                updateKeepScreenOn()
            }

            override fun nextGridCalculated() {
                mutableNextGridState.value = NextGridState.CALCULATED
                updateKeepScreenOn()
            }
        }

    private fun updateKeepScreenOn() {
        mutableKeepScreenOnState.value = mutableUiState.value.state == MainUiState.PLAYING && preferences.keepScreenOn()
    }

    override fun freshGridWasCreated() {
        mutableUiState.value = MainUiStateWithGrid(MainUiState.PLAYING, game.grid)
        updateKeepScreenOn()
    }

    private fun initialUiState() =
        MainUiStateWithGrid(
            if (game.grid.isSolved()) {
                MainUiState.ALREADY_SOLVED
            } else {
                MainUiState.PLAYING
            },
            game.grid,
        )

    override fun puzzleSolved() {
        mutableUiState.value = MainUiStateWithGrid(MainUiState.SOLVED, game.grid)
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
