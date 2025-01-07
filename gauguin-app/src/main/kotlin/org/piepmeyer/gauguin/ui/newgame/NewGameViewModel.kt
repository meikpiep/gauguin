package org.piepmeyer.gauguin.ui.newgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.calculation.GridPreviewCalculationService
import org.piepmeyer.gauguin.calculation.GridPreviewListener
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.GridCalculatorFactory
import org.piepmeyer.gauguin.difficulty.GameDifficultyRater
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

data class GridPreviewState(
    val grid: Grid,
    val stillCalculating: Boolean,
)

enum class GridCalculationAlgorithm {
    RandomGrid,
    MergingCages,
    ;

    companion object {
        fun fromMerging(useMergingAlgorithm: Boolean): GridCalculationAlgorithm = if (useMergingAlgorithm) MergingCages else RandomGrid
    }
}

data class GridVariantState(
    val variant: GameVariant,
    val calculationAlgorithm: GridCalculationAlgorithm,
)

class NewGameViewModel :
    ViewModel(),
    KoinComponent,
    GridPreviewListener {
    private val calculationService: GridCalculationService by inject()
    private val applicationPreferences: ApplicationPreferences by inject()
    private val gameLifecycle: GameLifecycle by inject()

    private val previewService = GridPreviewCalculationService()
    private val rater = GameDifficultyRater()

    private val mutablePreviewGridState = MutableStateFlow(initialPreviewService())
    private val mutableGameVariantState = MutableStateFlow(gridVariantState())

    val previewGridState: StateFlow<GridPreviewState> = mutablePreviewGridState.asStateFlow()
    val gameVariantState: StateFlow<GridVariantState> = mutableGameVariantState.asStateFlow()

    init {
        GridCalculatorFactory.alwaysUseNewAlgorithm = applicationPreferences.mergingCageAlgorithm

        previewService.addListener(this)
        previewService.calculateGrid(mutableGameVariantState.value.variant, viewModelScope)
    }

    private fun initialPreviewService(): GridPreviewState {
        GridCalculatorFactory.alwaysUseNewAlgorithm = applicationPreferences.mergingCageAlgorithm

        if (calculationService.hasCalculatedNextGrid(gameVariant())) {
            val grid = calculationService.consumeNextGrid()
            previewService.takeCalculatedGrid(grid)

            return GridPreviewState(grid, false)
        } else {
            return GridPreviewState(
                GridBuilder(2)
                    .addSingleCage(1, 0)
                    .addSingleCage(2, 1)
                    .addSingleCage(2, 2)
                    .addSingleCage(1, 3)
                    .createGrid(),
                true,
            )
        }
    }

    private fun gridVariantState(): GridVariantState {
        val gameVariant = gameVariant()
        val useMergingAlgorithm = !gameVariant.gridSize.isSquare || applicationPreferences.mergingCageAlgorithm

        return GridVariantState(gameVariant, GridCalculationAlgorithm.fromMerging(useMergingAlgorithm))
    }

    private fun gameVariant(): GameVariant =
        GameVariant(
            GridSize(
                applicationPreferences.gridWidth,
                applicationPreferences.gridHeigth,
            ),
            applicationPreferences.gameVariant,
        )

    override fun onCleared() {
        previewService.removeListener(this)

        super.onCleared()
    }

    override fun previewGridCreated(
        grid: Grid,
        previewStillCalculating: Boolean,
    ) {
        grid.options.numeralSystem = applicationPreferences.gameVariant.numeralSystem
        mutablePreviewGridState.value = GridPreviewState(grid, previewStillCalculating)
    }

    override fun previewGridCalculated(grid: Grid) {
        mutablePreviewGridState.value = GridPreviewState(grid, false)
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
        val grid = previewService.getGrid(variant)

        if (grid != null) {
            calculationService.variant = variant
            calculationService.setNextGrid(grid)
            gameLifecycle.startNewGame(grid)
        }

        gameLifecycle.postNewGame(startedFromMainActivityWithSameVariant = false)

        return grid != null
    }

    fun clearGrids() {
        previewService.clearGrids()
        previewService.calculateGrid(gameVariant(), viewModelScope)
        mutableGameVariantState.value = gridVariantState()
    }

    fun singleCellOptionsAvailable(): Boolean = mutableGameVariantState.value.calculationAlgorithm == GridCalculationAlgorithm.RandomGrid
}
