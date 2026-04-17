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
import org.piepmeyer.gauguin.calculation.GridPreviewListener
import org.piepmeyer.gauguin.creation.GridCalculatorFactory
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

sealed class GridPreviewState(
    val isStillCalculating: Boolean,
) {
    class GridPreviewNoGridAvailableYet : GridPreviewState(false)

    class GridPreviewStillCalculatingWithPreview(
        val previewGrid: Grid,
    ) : GridPreviewState(true)

    class GridPreviewStillCalculatingWithoutPreview : GridPreviewState(true)

    class GridPreviewCalculated(
        val grid: Grid,
    ) : GridPreviewState(false)
}

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
    KoinComponent,
    GridPreviewListener {
    private val previewService = GridPreviewCalculationService()

    private val mutablePreviewGridState = MutableStateFlow(initialPreviewState())
    private val mutableGameVariantState = MutableStateFlow(gridVariantState())
    private val mutableDifficultySelectionState = MutableStateFlow(initialDifficultySelectionState())

    val previewGridState: StateFlow<GridPreviewState> = mutablePreviewGridState.asStateFlow()
    val gameVariantState: StateFlow<GridVariantState> = mutableGameVariantState.asStateFlow()
    val difficultySelectionState: StateFlow<DifficultySelectionState> = mutableDifficultySelectionState.asStateFlow()

    init {
        GridCalculatorFactory.alwaysUseNewAlgorithm = applicationPreferences.mergingCageAlgorithm

        previewService.addListener(this)
        previewService.calculateGrid(mutableGameVariantState.value.variant, viewModelScope)
    }

    private fun initialPreviewState(): GridPreviewState {
        GridCalculatorFactory.alwaysUseNewAlgorithm = applicationPreferences.mergingCageAlgorithm

        val calculatedGrid = previewService.takeCalculatedGrid(calculationService, gameVariant())

        return if (calculatedGrid != null) {
            GridPreviewState.GridPreviewCalculated(calculatedGrid)
        } else {
            GridPreviewState.GridPreviewNoGridAvailableYet()
        }
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

    override fun onCleared() {
        previewService.removeListener(this)

        super.onCleared()
    }

    override fun previewGridCreated(
        grid: Grid,
        previewStillCalculating: Boolean,
    ) {
        grid.options.numeralSystem = applicationPreferences.gameOptionsVariant.numeralSystem

        mutablePreviewGridState.value =
            if (previewStillCalculating) {
                GridPreviewState.GridPreviewStillCalculatingWithPreview(grid)
            } else {
                GridPreviewState.GridPreviewCalculated(grid)
            }
    }

    override fun previewGridCalculated(grid: Grid) {
        mutablePreviewGridState.value = GridPreviewState.GridPreviewCalculated(grid)
    }

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
