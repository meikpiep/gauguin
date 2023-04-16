package com.holokenmod.calculation

import com.holokenmod.grid.Grid

interface GridCalculationListener {
    fun startingCurrentGridCalculation()
    fun startingNextGridCalculation()
    fun currentGridCalculated(currentGrid: Grid)
    fun nextGridCalculated(currentGrid: Grid)
}