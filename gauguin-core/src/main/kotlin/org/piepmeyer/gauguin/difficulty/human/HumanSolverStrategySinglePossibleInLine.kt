package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

class HumanSolverStrategySinglePossibleInLine : HumanSolverStrategy {
    override fun fillCells(grid: Grid) {
        grid.cells.filter { !it.isUserValueSet }
            .forEach { cell ->
                val validPossibleDigits =
                    grid.variant.possibleDigits.filter {
                        !grid.isUserValueUsedInSameRow(cell.cellNumber, it) &&
                            !grid.isUserValueUsedInSameColumn(cell.cellNumber, it)
                    }

                val differentPossibles = validPossibleDigits.toSet()

                if (differentPossibles.size == 1) {
                    println("Setting cell ${cell.cellNumber} to ${differentPossibles.single()}")
                    cell.userValue = differentPossibles.single()
                }
            }
    }
}
