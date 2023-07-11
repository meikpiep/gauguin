package com.srlee.dlx

import com.holokenmod.GridSolver
import com.holokenmod.grid.Grid

class MathDokuDLXSolver: GridSolver {
    override fun solve(grid: Grid, isPreSolved: Boolean): Int {
        return MathDokuDLX(grid).solve(DLX.SolveType.MULTIPLE)
    }
}