package org.piepmeyer.gauguin.ui.newgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.calculation.GridPreviewCalculationService
import org.piepmeyer.gauguin.calculation.GridPreviewState
import org.piepmeyer.gauguin.creation.GridCalculatorFactory
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

enum class GridCalculationAlgorithm {
    RandomGrid,
    MergingCages,
    ;

    companion object {
        fun fromMerging(useMergingAlgorithm: Boolean): GridCalculationAlgorithm = if (useMergingAlgorithm) MergingCages else RandomGrid
    }
}

enum class DifficultySelectionState {
    SINGLE_SELECTION,
    MULTI_SELECTION,
}

data class GridVariantState(
    val variant: GameVariant,
    val calculationAlgorithm: GridCalculationAlgorithm,
)

class NewGameViewModel(
    @InjectedParam val calculationService: GridCalculationService,
    @InjectedParam val applicationPreferences: ApplicationPreferences,
    @InjectedParam val gameLifecycle: GameLifecycle,
) : ViewModel(),
    KoinComponent {
    private val previewService =
        GridPreviewCalculationService(calculationService, applicationPreferences, gameVariant())

    private val mutableGameVariantState = MutableStateFlow(gridVariantState())
    private val mutableDifficultySelectionState = MutableStateFlow(initialDifficultySelectionState())

    val gameVariantState: StateFlow<GridVariantState> = mutableGameVariantState.asStateFlow()
    val difficultySelectionState: StateFlow<DifficultySelectionState> = mutableDifficultySelectionState.asStateFlow()
    val previewGridState: StateFlow<GridPreviewState> = previewService.previewGridState

    init {
        GridCalculatorFactory.alwaysUseNewAlgorithm = applicationPreferences.mergingCageAlgorithm

        previewService.calculateGrid(mutableGameVariantState.value.variant, viewModelScope)
    }

    private fun gridVariantState(): GridVariantState {
        val gameVariant = gameVariant()
        val useMergingAlgorithm = !gameVariant.gridSize.isSquare || applicationPreferences.mergingCageAlgorithm

        return GridVariantState(gameVariant, GridCalculationAlgorithm.fromMerging(useMergingAlgorithm))
    }

    private fun initialDifficultySelectionState(): DifficultySelectionState =
        if (DifficultySetting.isApplicableToSingleSelection(applicationPreferences.difficultiesSetting)) {
            DifficultySelectionState.SINGLE_SELECTION
        } else {
            DifficultySelectionState.MULTI_SELECTION
        }

    private fun gameVariant(): GameVariant =
        GameVariant(
            GridSize(
                applicationPreferences.gridWidth,
                applicationPreferences.gridHeigth,
            ),
            applicationPreferences.gameOptionsVariant,
        )

    fun calculateGrid() {
        val oldState = mutableGameVariantState.value
        val newState = gridVariantState()

        if (oldState != newState) {
            mutableGameVariantState.value = newState
            previewService.calculateGrid(newState.variant, viewModelScope)
        }
    }

    fun startNewGame(): Boolean {
        val variant = gameVariant()
        val previewGrid = previewService.getGrid(variant)

        previewGrid?.let {
            viewModelScope.launch {
                calculationService.consumeNextGridIfMatching(it)
            }
        }

        val grid = previewGrid?.copyWithEmptyUserValues()

        gameLifecycle.startNewCalculatedGame(grid, variant)

        return grid != null
    }

    fun clearGrids() {
        previewService.clearGrids()
        previewService.calculateGrid(gameVariant(), viewModelScope)
        mutableGameVariantState.value = gridVariantState()
    }

    fun singleCellOptionsAvailable(): Boolean = mutableGameVariantState.value.calculationAlgorithm == GridCalculationAlgorithm.RandomGrid

    fun updateDifficultyMultiSelection(value: DifficultySelectionState) {
        if (value == DifficultySelectionState.SINGLE_SELECTION) {
            if (!DifficultySetting.isApplicableToSingleSelection(applicationPreferences.difficultiesSetting)) {
                applicationPreferences.difficultiesSetting = DifficultySetting.all()
            }
        }

        mutableDifficultySelectionState.value = value
        calculateGrid()
    }
}
