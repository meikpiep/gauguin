package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.grid.Grid

class HumanSolverStrategySinglePossibleInCage : HumanSolverStrategy {
    override fun fillCells(grid: Grid) {
        grid.cages
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

                if (differentPossibles.size == 1) {
                    println("Setting cell ${cage.getCell(0).cellNumber} to ${differentPossibles.single()}")
                    cage.getCell(0).userValue = differentPossibles.single()
                }
            }
    }
}
