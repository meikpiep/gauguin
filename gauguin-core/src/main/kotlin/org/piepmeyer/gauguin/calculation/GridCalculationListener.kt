package org.piepmeyer.gauguin.calculation

import org.piepmeyer.gauguin.grid.Grid

interface GridCalculationListener {
    fun startingCurrentGridCalculation()

    fun startingNextGridCalculation()

    fun currentGridCalculated(currentGrid: Grid)

    fun nextGridCalculated()

    fun pushGridToMainActivity(grid: Grid)
}
