package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid

class RemoveImpossibleCombination : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        grid.cages
            .filter { it.cells.any { !it.isUserValueSet } }
            .forEach { cage ->
                val creator = GridSingleCageCreator(grid.variant, cage)

                val validPossibles =
                    creator.possibleCombinations.filter { possibleCombination ->
                        cage.cells.withIndex().all { cell ->
                            if (cell.value.isUserValueSet) {
                                cell.value.userValue == possibleCombination[cell.index]
                            } else {
                                cell.value.possibles.contains(possibleCombination[cell.index])
                            }
                        }
                    }

                for (cellNumber in 0..<cage.cells.size) {
                    val differentPossibles = validPossibles.map { it[cellNumber] }.toSet()

                    for (possible in cage.getCell(cellNumber).possibles) {
                        if (!differentPossibles.contains(possible)) {
                            cage.getCell(cellNumber).possibles -= possible

                            return true
                        }
                    }
                }
            }

        return false
    }
}
