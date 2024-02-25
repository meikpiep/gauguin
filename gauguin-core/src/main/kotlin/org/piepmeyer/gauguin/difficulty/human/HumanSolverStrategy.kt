package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

fun interface HumanSolverStrategy {
    fun fillCells(grid: Grid): Boolean
}
