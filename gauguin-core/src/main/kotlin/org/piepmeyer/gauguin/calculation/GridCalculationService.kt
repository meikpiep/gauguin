package org.piepmeyer.gauguin.calculation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.piepmeyer.gauguin.creation.GridCalculatorFactory
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
        invokeAfterNewGridWasCreated: (Grid) -> Unit,
    ) {
        nextGrid = null
        this.variant = variant
        calculateCurrentGrid(scope, invokeAfterNewGridWasCreated)
        calculateNextGrid(scope)
    }

    private fun calculateCurrentGrid(
        scope: CoroutineScope,
        invokeAfterNewGridWasCreated: (Grid) -> Unit,
    ) {
        scope.launch(dispatcher) {
            listeners.forEach { it.startingCurrentGridCalculation() }

            val newGrid = GridCalculatorFactory().createCalculator(variant).calculate()
            invokeAfterNewGridWasCreated.invoke(newGrid)

            listeners.forEach { it.currentGridCalculated() }
        }
    }

    fun calculateNextGrid(scope: CoroutineScope) {
        if (nextGrid != null) return

        scope.launch(dispatcher) {
            listeners.forEach { it.startingNextGridCalculation() }

            nextGrid = GridCalculatorFactory().createCalculator(variant).calculate()

            listeners.forEach { it.nextGridCalculated() }
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
