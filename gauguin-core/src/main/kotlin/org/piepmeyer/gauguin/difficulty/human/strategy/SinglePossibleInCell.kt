package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

class SinglePossibleInCell : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Pair<Boolean, List<GridCell>?> {
        grid.cells
            .filter { !it.isUserValueSet }
            .firstOrNull { it.possibles.size == 1 }
            ?.let {
                val changedCells = grid.setUserValueAndRemovePossibles(it, it.possibles.first())

                return HumanSolverStrategy.successCellsChanged(changedCells)
            }

        return HumanSolverStrategy.nothingChanged()
    }
}
