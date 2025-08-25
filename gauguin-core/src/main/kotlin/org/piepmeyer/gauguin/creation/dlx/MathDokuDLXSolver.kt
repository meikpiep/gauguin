package org.piepmeyer.gauguin.creation.dlx

import org.piepmeyer.gauguin.grid.Grid

class MathDokuDLXSolver {
    suspend fun solve(grid: Grid): Int = MathDokuDLX(grid).solve(DLX.SolveType.MULTIPLE)
}
