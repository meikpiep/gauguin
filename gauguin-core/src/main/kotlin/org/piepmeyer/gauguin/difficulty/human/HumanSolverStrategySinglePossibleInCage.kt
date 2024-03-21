package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.grid.Grid

class HumanSolverStrategySinglePossibleInCage : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        grid.cages
            .filter { it.cells.any { !it.isUserValueSet } }
            .forEach { cage ->
                val creator = GridSingleCageCreator(grid.variant, cage)

                val validPossibles =
                    creator.possibleCombinations.filter { possibleNum ->
                        cage.cells.withIndex().all { cell ->
                            if (cell.value.isUserValueSet) {
                                cell.value.userValue == possibleNum[cell.index]
                            } else {
                                cell.value.possibles.contains(possibleNum[cell.index])
                            }
                        }
                    }

                for (cellNumber in 0..<cage.cells.size) {
                    val differentPossibles = validPossibles.map { it[cellNumber] }.toSet()

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

    override fun difficulty(): Int = 5
}
