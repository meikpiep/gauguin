package com.holokenmod

import com.holokenmod.grid.Grid

interface GridSolver {
    fun solve(grid: Grid, isPreSolved: Boolean): Int
}
