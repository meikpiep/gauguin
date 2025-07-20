package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

fun interface HumanSolverStrategy {
    fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Pair<Boolean, List<GridCell>?>

    fun fillCellsWithNewCache(grid: Grid): Boolean {
        val cache = HumanSolverCacheImpl(grid)
        cache.initialize()
        cache.validateAllEntries()

        return fillCells(grid, cache).first
    }

    companion object {
        fun nothingChanged(): Pair<Boolean, List<GridCell>?> = Pair(false, null)

        fun successCellsChanged(changedCells: List<GridCell>): Pair<Boolean, List<GridCell>?> = Pair(true, changedCells)
    }
}
