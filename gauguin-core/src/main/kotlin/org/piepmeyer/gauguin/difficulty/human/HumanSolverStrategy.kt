package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

fun interface HumanSolverStrategy {
    fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Boolean

    fun fillCellsWithNewCache(grid: Grid): Boolean {
        val cache = HumanSolverCache(grid)
        cache.initialize()

        return fillCells(grid, cache)
    }
}
