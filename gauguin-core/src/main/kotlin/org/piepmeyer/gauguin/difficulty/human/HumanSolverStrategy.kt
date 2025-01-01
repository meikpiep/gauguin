package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

fun interface HumanSolverStrategy {
    fun fillCells(
        grid: Grid,
        cache: PossiblesCache,
    ): Boolean

    fun fillCellsWithNewCache(grid: Grid): Boolean {
        val cache = PossiblesCache(grid)
        cache.initialize()

        return fillCells(grid, cache)
    }
}
