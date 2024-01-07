package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

interface HumanSolverStrategy {
    fun fillCells(grid: Grid)
}
