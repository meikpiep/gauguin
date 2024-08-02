package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid

/**
 * Looks out if a cage's cells contain possibles which are not included in any
 * valid combination. If so, deletes these possibles out of all the cage's
 * cells.
 */
class RemoveImpossibleCageCombinations : HumanSolverStrategy {
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

                var foundPossibles = false

                cage.cells.forEachIndexed { cellIndex, cell ->
                    val differentPossibles = validPossibles.map { it[cellIndex] }.toSet()

                    for (possible in cell.possibles) {
                        if (!differentPossibles.contains(possible)) {
                            cage.getCell(cellIndex).possibles -= possible

                            foundPossibles = true
                        }
                    }
                }

                if (foundPossibles) {
                    return true
                }
            }

        return false
    }
}
