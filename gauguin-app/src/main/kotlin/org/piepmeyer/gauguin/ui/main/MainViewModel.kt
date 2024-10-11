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

enum class MainUiState {
    PLAYING,
    CALCULATING_NEW_GRID,
    SOLVED,
    SOLVED_BY_REVEAL,
}

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

    private val _uiState = MutableStateFlow(MainUiState.PLAYING)
    private val _nextGridState = MutableStateFlow(NextGridState.CALCULATED)
    private val _fastFinishingModeState = MutableStateFlow(FastFinishingModeState.INACTIVE)

    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    val nextGridState: StateFlow<NextGridState> = _nextGridState.asStateFlow()
    val fastFinishingModeState: StateFlow<FastFinishingModeState> = _fastFinishingModeState.asStateFlow()

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
                _uiState.value = MainUiState.CALCULATING_NEW_GRID
            }

            override fun currentGridCalculated() {
                _uiState.value = MainUiState.PLAYING
            }

            override fun startingNextGridCalculation() {
                _nextGridState.value = NextGridState.CURRENTLY_CALCULATING
            }

            override fun nextGridCalculated() {
                _nextGridState.value = NextGridState.CALCULATED
            }
        }

    override fun freshGridWasCreated() {
        _uiState.value = MainUiState.PLAYING
    }

    override fun puzzleSolved(troughReveal: Boolean) {
        _uiState.value =
            if (troughReveal) {
                MainUiState.SOLVED_BY_REVEAL
            } else {
                MainUiState.SOLVED
            }
    }

    override fun changedGameMode() {
        _fastFinishingModeState.value =
            if (game.isInFastFinishingMode()) {
                FastFinishingModeState.ACTIVE
            } else {
                FastFinishingModeState.INACTIVE
            }
    }
}
