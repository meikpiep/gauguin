package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

class NakedPair : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        val cellsWithoutUserValue = grid.cells.filter { !it.isUserValueSet }

        cellsWithoutUserValue.forEach { cell ->
            cellsWithoutUserValue.forEach { otherCell ->
                if (isNakedPair(cell, otherCell)) {
                    val possibles = cell.possibles

                    val cellsOfSameRowOrColumn =
                        if (cell.row == otherCell.row) {
                            grid.getCellsAtSameRow(cell) - otherCell
                        } else {
                            grid.getCellsAtSameColumn(cell) - otherCell
                        }

                    val cellsWithPossibles =
                        cellsOfSameRowOrColumn
                            .filter { !it.isUserValueSet }
                            .filter { it.possibles.intersect(possibles).isNotEmpty() }

                    if (cellsWithPossibles.isNotEmpty()) {
                        cellsWithPossibles.forEach {
                            it.possibles -= possibles
                        }

                        println("Naked pair found: ${cell.cellNumber} and ${otherCell.cellNumber}")

                        return true
                    }
                }
            }
        }

        return false
    }

    private fun isNakedPair(
        cell: GridCell,
        otherCell: GridCell,
    ) = cell != otherCell &&
        (cell.row == otherCell.row || cell.column == otherCell.column) &&
        cell.possibles.size == 2 &&
        otherCell.possibles.size == 2 &&
        cell.possibles.containsAll(otherCell.possibles)

    override fun difficulty(): Int = 25
}
