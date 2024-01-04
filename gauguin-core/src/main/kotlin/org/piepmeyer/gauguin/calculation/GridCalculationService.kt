package org.piepmeyer.gauguin.calculation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.piepmeyer.gauguin.creation.GridCalculator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.GameVariant

class GridCalculationService(
    var variant: GameVariant,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val listeners = mutableListOf<GridCalculationListener>()
    var nextGrid: Grid? = null

    fun addListener(listener: GridCalculationListener) {
        listeners += listener
    }

    fun calculateCurrentAndNextGrids(
        variant: GameVariant,
        scope: CoroutineScope,
    ) {
        nextGrid = null
        this.variant = variant
        calculateCurrentGrid(scope)
        calculateNextGrid(scope)
    }

    private fun calculateCurrentGrid(scope: CoroutineScope) {
        scope.launch(dispatcher) {
            listeners.forEach { it.startingCurrentGridCalculation() }

            val newGrid = GridCalculator(variant).calculate()

            listeners.forEach { it.currentGridCalculated(newGrid) }
        }
    }

    fun calculateNextGrid(scope: CoroutineScope) {
        if (nextGrid != null) return

        scope.launch(dispatcher) {
            listeners.forEach { it.startingNextGridCalculation() }

            nextGrid = GridCalculator(variant).calculate()

            listeners.forEach { it.nextGridCalculated(nextGrid!!) }
        }
    }

    fun hasCalculatedNextGrid(variantParam: GameVariant): Boolean {
        return nextGrid != null && variantParam == variant
    }

    fun consumeNextGrid(): Grid {
        val grid = nextGrid!!
        nextGrid = null
        return grid
    }
}
