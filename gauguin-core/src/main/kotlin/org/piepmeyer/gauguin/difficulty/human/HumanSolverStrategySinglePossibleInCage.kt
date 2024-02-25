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
                    creator.possibleNums.filter { possibleNum ->
                        cage.cells.withIndex().all { cell ->
                            !grid.isUserValueUsedInSameRow(cell.value.cellNumber, possibleNum[cell.index]) &&
                                !grid.isUserValueUsedInSameColumn(cell.value.cellNumber, possibleNum[cell.index])
                        }
                    }

                val differentPossibles = validPossibles.map { it[0] }.toSet()

                println("Cage ${cage.id}: $differentPossibles")

                if (differentPossibles.size == 1 && !cage.getCell(0).isUserValueSet) {
                    println("Setting cell ${cage.getCell(0).cellNumber} to ${differentPossibles.single()}")
                    grid.setUserValueAndRemovePossibles(cage.getCell(0), differentPossibles.single())

                    return true
                }
            }

        return false
    }
}
