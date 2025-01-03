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
                        val possibles = validPossibles.map { it[cellNumber] }

                        if (possibles.isNotEmpty() && (possibles.size == 1 || possibles.none { it != possibles.first() })) {
                            grid.setUserValueAndRemovePossibles(
                                cage.getCell(cellNumber),
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
