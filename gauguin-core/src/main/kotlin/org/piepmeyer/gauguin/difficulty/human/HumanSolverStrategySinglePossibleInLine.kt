package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

class HumanSolverStrategySinglePossibleInLine : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        grid.cells.filter { !it.isUserValueSet }
            .forEach { cell ->

                cell.possibles.forEach { possible ->
                    if (grid.getCellsAtSameRow(cell).map {
                            it.possibles
                        }.none {
                            it.contains(possible)
                        } || grid.getCellsAtSameColumn(cell).map { it.possibles }.none { it.contains(possible) }
                    ) {
                        grid.setUserValueAndRemovePossibles(cell, possible)

                        return true
                    }
                }
            }

        return false
    }

    override fun difficulty(): Int = 10
}
