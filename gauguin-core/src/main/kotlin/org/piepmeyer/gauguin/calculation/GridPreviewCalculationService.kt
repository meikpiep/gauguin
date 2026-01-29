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
import java.util.WeakHashMap

private val logger = KotlinLogging.logger {}

class GridPreviewCalculationService(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val grids: MutableMap<GameVariant, Grid> = WeakHashMap()
    private var listeners = mutableListOf<GridPreviewListener>()
    private var previewCalculator: GridPreviewCalculator? = null

    fun getGrid(gameVariant: GameVariant): Grid? = grids[gameVariant]

    fun takeCalculatedGrid(grid: Grid) {
        grids[grid.variant] = grid
        listeners.forEach { it.previewGridCreated(grid, false) }
    }

    fun calculateGrid(
        variant: GameVariant,
        scope: CoroutineScope,
    ) {
        previewCalculator?.cancelCalculation()

        scope.launch(dispatcher) {
            with(this + CoroutineName("GridPreview-$variant")) {
                val calculator = GridPreviewCalculator(variant, listeners, this)
                previewCalculator = calculator

                calculator.calculateGrid(grids)
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
        grids.clear()

        previewCalculator?.cancelCalculation()
        previewCalculator = null
    }

    fun removeGrid(gridToRemove: Grid) {
        grids.remove(gridToRemove.variant)
    }
}
