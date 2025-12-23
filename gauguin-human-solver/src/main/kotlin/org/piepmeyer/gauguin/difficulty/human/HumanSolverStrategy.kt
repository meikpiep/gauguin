package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

sealed interface HumanSolverStrategyResult {
    fun madeChanges(): Boolean

    class NothingChanged : HumanSolverStrategyResult {
        override fun madeChanges(): Boolean = false
    }

    class Success(
        val changedCells: Collection<GridCell>,
    ) : HumanSolverStrategyResult {
        override fun madeChanges(): Boolean = true
    }
}

fun interface HumanSolverStrategy {
    fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): HumanSolverStrategyResult

    fun fillCellsWithNewCache(grid: Grid): Boolean {
        val cache = HumanSolverCacheImpl(grid)
        cache.initialize()
        cache.validateAllEntries()

        return fillCells(grid, cache) is HumanSolverStrategyResult.Success
    }

    fun fillCellsWithNewCacheReturningDetails(grid: Grid): HumanSolverStrategyResult {
        val cache = HumanSolverCacheImpl(grid)
        cache.initialize()
        cache.validateAllEntries()

        return fillCells(grid, cache)
    }
}
