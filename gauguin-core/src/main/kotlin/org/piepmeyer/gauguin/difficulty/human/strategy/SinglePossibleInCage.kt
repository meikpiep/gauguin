package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid

class SinglePossibleInCage : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Boolean {
        grid.cages
            .filter { it.cells.any { !it.isUserValueSet } }
            .forEach { cage ->
                val validPossibles = cache.possibles(cage)

                cage.cells.forEachIndexed { index, cell ->
                    if (!cell.isUserValueSet) {
                        val possibles = validPossibles.map { it[index] }

                        if (possibles.isNotEmpty() && (possibles.size == 1 || possibles.none { it != possibles.first() })) {
                            grid.setUserValueAndRemovePossibles(
                                cell,
                                possibles.first(),
                            )

                            return true
                        }
                    }
                }
            }

        return false
    }
}
