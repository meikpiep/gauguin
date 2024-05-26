package org.piepmeyer.gauguin.calculation

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.piepmeyer.gauguin.creation.GridCalculatorFactory
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.GameVariant

private val logger = KotlinLogging.logger {}

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
            logger.info { "Calculating current grid of $variant" }
            listeners.forEach { it.startingCurrentGridCalculation() }

            logger.info { "Calculating current grid via factory of $variant" }
            val newGrid = GridCalculatorFactory().createCalculator(variant).calculate()
            invokeAfterNewGridWasCreated.invoke(newGrid)
            logger.info { "Finished calculating current grid via factory of $variant" }

            listeners.forEach { it.currentGridCalculated() }
            logger.info { "Finished calculating current grid of $variant" }
        }
    }

    fun calculateNextGrid(scope: CoroutineScope) {
        if (nextGrid != null) return

        scope.launch(dispatcher) {
            logger.info { "Calculating next grid of $variant" }
            listeners.forEach { it.startingNextGridCalculation() }

            logger.info { "Calculating next grid via factory of $variant" }
            nextGrid = GridCalculatorFactory().createCalculator(variant).calculate()
            logger.info { "Finished calculating next grid via factory of $variant" }

            listeners.forEach { it.nextGridCalculated() }
            logger.info { "Finished calculating next grid of $variant" }
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

    fun setNextGrid(grid: Grid) {
        nextGrid = grid
    }
}
