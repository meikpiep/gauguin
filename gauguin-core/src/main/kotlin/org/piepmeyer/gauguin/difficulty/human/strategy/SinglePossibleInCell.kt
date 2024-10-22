package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesCache
import org.piepmeyer.gauguin.grid.Grid

class SinglePossibleInCell : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: PossiblesCache,
    ): Boolean {
        grid.cells
            .filter { !it.isUserValueSet }
            .firstOrNull { it.possibles.size == 1 }
            ?.let {
                grid.setUserValueAndRemovePossibles(it, it.possibles.first())

                return true
            }

        return false
    }
}
