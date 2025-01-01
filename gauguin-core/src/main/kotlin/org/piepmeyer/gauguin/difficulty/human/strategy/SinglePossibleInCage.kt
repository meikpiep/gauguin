package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesCache
import org.piepmeyer.gauguin.grid.Grid

class SinglePossibleInCage : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: PossiblesCache,
    ): Boolean {
        grid.cages
            .filter { it.cells.any { !it.isUserValueSet } }
            .forEach { cage ->
                val validPossibles = cache.possibles(cage)

                for (cellNumber in 0..<cage.cells.size) {
                    val differentPossibles = validPossibles.map { it[cellNumber] }.distinct()

                    if (differentPossibles.size == 1 && !cage.getCell(cellNumber).isUserValueSet) {
                        grid.setUserValueAndRemovePossibles(
                            cage.getCell(cellNumber),
                            differentPossibles.single(),
                        )

                        return true
                    }
                }
            }

        return false
    }
}
