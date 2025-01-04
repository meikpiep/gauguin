package org.piepmeyer.gauguin.creation

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

class GridRandomizer(
    private val shuffler: PossibleDigitsShuffler,
    private val grid: Grid,
) {
    private enum class FillMode {
        HORIZONTAL,
        VERTICAL,
    }

    private var fillMode =
        if (grid.gridSize.height > grid.gridSize.width) {
            FillMode.VERTICAL
        } else {
            FillMode.HORIZONTAL
        }

    fun createGridValues() {
        createCells(0, 0)
    }

    private fun createCells(
        column: Int,
        row: Int,
    ): Boolean {
        if (column == grid.gridSize.width ||
            row == grid.gridSize.height
        ) {
            return true
        }
        val cell = grid.getValidCellAt(row, column)
        val possibleDigits =
            getShuffledPossibleDigits(
                grid,
                column + row * grid.gridSize.width,
            )

        for (digit in possibleDigits) {
            cell.value = digit
            var nextRow = row
            var nextColumn = column
            if (fillMode == FillMode.HORIZONTAL) {
                nextColumn++
                if (nextColumn == grid.gridSize.width) {
                    nextColumn = 0
                    nextRow++
                }
            } else {
                nextRow++
                if (nextRow == grid.gridSize.height) {
                    nextRow = 0
                    nextColumn++
                }
            }
            if (createCells(nextColumn, nextRow)) {
                return true
            }
        }
        cell.value = GridCell.NO_VALUE_SET
        return false
    }

    private fun getShuffledPossibleDigits(
        grid: Grid,
        cellNumber: Int,
    ): List<Int> {
        val possibleDigits =
            if (cellNumber == 0) {
                grid.variant.possibleDigits
            } else {
                grid.variant.possibleDigits
                    .filter {
                        !grid.isValueUsedInSameRow(cellNumber, it) &&
                            !grid.isValueUsedInSameColumn(cellNumber, it)
                    }.toSet()
            }

        return if (possibleDigits.isEmpty()) {
            emptyList()
        } else {
            shuffler.shufflePossibleDigits(possibleDigits)
        }
    }
}
