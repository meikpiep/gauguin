package org.piepmeyer.gauguin.creation.dlx

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.grid.Grid

class DLXGrid(
    val grid: Grid,
) {
    val gridSize = grid.gridSize
    val digitSetting = grid.options.digitSetting
    val possibleDigits = digitSetting.getPossibleDigits(grid.gridSize)

    val creators =
        grid.cages.map {
            GridSingleCageCreator(grid.variant, it)
        }

    val isFilled = grid.cells.all { it.value != null }

    fun columnAndRowConstraints(
        indexOfDigit: Int,
        creator: GridSingleCageCreator,
        cellOfCage: Int,
    ): Pair<Int, Int> =
        columnAndRowConstraints(
            indexOfDigit,
            creator.getCell(cellOfCage).column,
            creator.getCell(cellOfCage).row,
        )

    fun columnAndRowConstraints(
        indexOfDigit: Int,
        column: Int,
        row: Int,
    ): Pair<Int, Int> {
        val columnConstraint = grid.gridSize.width * indexOfDigit + column
        val rowConstraint = (
            grid.gridSize.width * possibleDigits.size +
                grid.gridSize.height * indexOfDigit + row
        )

        return Pair(columnConstraint, rowConstraint)
    }

    fun cageConstraint(cageId: Int): Int = possibleDigits.size * (grid.gridSize.width + grid.gridSize.height) + cageId
}
