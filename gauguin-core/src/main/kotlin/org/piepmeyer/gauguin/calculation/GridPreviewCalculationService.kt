package org.piepmeyer.gauguin.calculation

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.GameVariant

private val logger = KotlinLogging.logger {}

class GridPreviewCalculationService(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val cache = GridPreviewCache()
    private var listeners = mutableListOf<GridPreviewListener>()
    private var previewCalculator: GridPreviewCalculator? = null

    fun getGrid(gameVariant: GameVariant): Grid? = cache.getGrid(gameVariant)

    fun takeCalculatedGrid(grid: Grid) {
        cache.putGrid(grid)
        listeners.forEach { it.previewGridCreated(grid, false) }
    }

    fun calculateGrid(
        variant: GameVariant,
        scope: CoroutineScope,
    ) {
        previewCalculator?.cancelCalculation()

        cache.getGrid(variant)?.let { grid ->
            logger.debug { "Returning already calculated grid." }

            listeners.forEach { it.previewGridCreated(grid, false) }
            return
        }

        scope.launch(dispatcher) {
            with(this + CoroutineName("GridPreview-$variant")) {
                val calculator = GridPreviewCalculator(variant, listeners, cache, this)
                previewCalculator = calculator

                calculator.calculateGrid()
            }
        }
    }

    fun addListener(listener: GridPreviewListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: GridPreviewListener) {
        listeners.remove(listener)
    }

    fun clearGrids() {
        cache.clear()

        previewCalculator?.cancelCalculation()
        previewCalculator = null
    }
}
