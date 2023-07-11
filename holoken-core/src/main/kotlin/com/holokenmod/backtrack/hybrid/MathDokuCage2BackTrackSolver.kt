package com.holokenmod.backtrack.hybrid

import com.holokenmod.GridSolver
import com.holokenmod.grid.Grid

class MathDokuCage2BackTrackSolver: GridSolver {
    override fun solve(grid: Grid, isPreSolved: Boolean): Int {
        return MathDokuCage2BackTrack(grid, isPreSolved).solve()
    }
}