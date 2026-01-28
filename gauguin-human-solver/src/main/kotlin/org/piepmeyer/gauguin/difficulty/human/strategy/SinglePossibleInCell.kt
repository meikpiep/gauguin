package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategyResult
import org.piepmeyer.gauguin.grid.Grid

/**
 * Finds a cell which only contains one single possible and uses this as the cells user value.
 */
class SinglePossibleInCell : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): HumanSolverStrategyResult {
        grid.cells
            .filter { !it.isUserValueSet }
            .firstOrNull { it.possibles.size == 1 }
            ?.let {
                val changedCells = grid.setUserValueAndRemovePossibles(it, it.possibles.first())

                return HumanSolverStrategyResult.Success(changedCells)
            }

        return HumanSolverStrategyResult.NothingChanged()
    }
}
