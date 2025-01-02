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

                for (cellNumber in 0..<cage.cells.size) {
                    if (!cage.getCell(cellNumber).isUserValueSet) {
                        val differentPossibles = validPossibles.map { it[cellNumber] }.distinct()

                        if (differentPossibles.size == 1) {
                            grid.setUserValueAndRemovePossibles(
                                cage.getCell(cellNumber),
                                differentPossibles.single(),
                            )

                            return true
                        }
                    }
                }
            }

        return false
    }
}
