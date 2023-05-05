package com.holokenmod.calculation

import com.holokenmod.creation.GridCalculator
import com.holokenmod.grid.Grid
import com.holokenmod.options.GameVariant
import java.util.function.Consumer

class GridCalculationService(
    private var variant: GameVariant
) {
    private val listeners = mutableListOf<GridCalculationListener>()
    private var nextGrid: Grid? = null

    fun addListener(listener: GridCalculationListener) {
        listeners += listener
    }

    fun calculateCurrentAndNextGrids(variant: GameVariant) {
        nextGrid = null
        this.variant = variant
        calculateCurrentGrid()
        calculateNextGrid()
    }

    private fun calculateCurrentGrid() {
        listeners.forEach(Consumer { obj: GridCalculationListener -> obj.startingCurrentGridCalculation() })
        val creator = GridCalculator(variant)
        val newGrid = creator.calculate()
        listeners.forEach(Consumer { listener: GridCalculationListener ->
            listener.currentGridCalculated(
                newGrid
            )
        })
    }

    fun calculateNextGrid() {
        listeners.forEach(Consumer { obj: GridCalculationListener -> obj.startingNextGridCalculation() })
        val creator = GridCalculator(variant)
        val grid = creator.calculate()
        nextGrid = grid
        listeners.forEach(Consumer { listener: GridCalculationListener ->
            listener.nextGridCalculated(
                grid
            )
        })
    }

    fun hasCalculatedNextGrid(variantParam: GameVariant): Boolean {
        return nextGrid != null && variantParam == variant
    }

    fun consumeNextGrid(): Grid {
        val grid = nextGrid!!
        nextGrid = null
        return grid
    }

    fun setVariant(variant: GameVariant) {
        this.variant = variant
    }

    fun setNextGrid(grid: Grid) {
        nextGrid = grid
    }
}