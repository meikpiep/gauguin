package org.piepmeyer.gauguin.calculation

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import org.piepmeyer.gauguin.creation.GridCalculatorFactory
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

private val logger = KotlinLogging.logger {}

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

class GridPreviewCalculationService(
    private val calculationService: GridCalculationService,
    private val applicationPreferences: ApplicationPreferences,
    private val gameVariant: GameVariant,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val cache = GridPreviewCache()

    private val mutablePreviewGridState = MutableStateFlow(initialPreviewState())
    val previewGridState: StateFlow<GridPreviewState> = mutablePreviewGridState.asStateFlow()

    private var previewCalculator: GridPreviewCalculator? = null

    fun getGrid(gameVariant: GameVariant): Grid? = cache.getGrid(gameVariant)

    private fun initialPreviewState(): GridPreviewState {
        GridCalculatorFactory.alwaysUseNewAlgorithm = applicationPreferences.mergingCageAlgorithm

        val calculatedGrid = takeCalculatedGrid(calculationService, gameVariant)

        return if (calculatedGrid != null) {
            GridPreviewState.GridPreviewCalculated(calculatedGrid)
        } else {
            GridPreviewState.GridPreviewNoGridAvailableYet()
        }
    }

    private fun takeCalculatedGrid(
        calculationService: GridCalculationService,
        variant: GameVariant,
    ): Grid? {
        if (!calculationService.hasCalculatedNextGrid(variant)) {
            logger.debug { "Did not find a matching grid in calculation service." }
            return null
        }

        logger.debug { "Found a matching grid in calculation service, will reuse it." }

        val grid =
            runBlocking {
                calculationService.getNextGrid()
            }

        cache.putGrid(grid)

        return grid
    }

    fun calculateGrid(
        variant: GameVariant,
        scope: CoroutineScope,
    ) {
        previewCalculator?.cancelCalculation()

        cache.getGrid(variant)?.let { grid ->
            logger.debug { "Returning already calculated grid." }

            previewGridCreated(grid, false)
            return
        }

        scope.launch(dispatcher) {
            with(this + CoroutineName("GridPreview-$variant")) {
                val calculator = GridPreviewCalculator(variant, this@GridPreviewCalculationService, cache, this)
                previewCalculator = calculator

                calculator.calculateGrid()
            }
        }
    }

    fun previewGridCreated(
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

    fun previewGridCalculated(grid: Grid) {
        mutablePreviewGridState.value = GridPreviewState.GridPreviewCalculated(grid)
    }

    fun clearGrids() {
        cache.clear()

        previewCalculator?.cancelCalculation()
        previewCalculator = null
    }
}
