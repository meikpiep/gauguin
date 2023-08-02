package com.holokenmod.creation.dlx

import com.holokenmod.creation.cage.GridSingleCageCreator
import com.holokenmod.grid.Grid

class DLXGrid(
    val grid: Grid
) {
    val gridSize = grid.gridSize
    val digitSetting = grid.options.digitSetting
    val possibleDigits = digitSetting.getPossibleDigits(grid.gridSize)

    val creators = grid.cages.map {
        GridSingleCageCreator(grid.variant, it)
    }

    fun columnAndRowConstraints(
        indexOfDigit: Int,
        creator: GridSingleCageCreator,
        cellOfCage: Int
    ): Pair<Int, Int> {
        return columnAndRowConstraints(
            indexOfDigit,
            creator.getCell(cellOfCage).column,
            creator.getCell(cellOfCage).row
        )
    }

    fun columnAndRowConstraints(
        indexOfDigit: Int,
        column: Int,
        row: Int
    ): Pair<Int, Int> {
        val columnConstraint = grid.gridSize.width * indexOfDigit + column
        val rowConstraint = (
            grid.gridSize.width * possibleDigits.size +
                grid.gridSize.height * indexOfDigit + row
            )

        return Pair(columnConstraint, rowConstraint)
    }

    fun cageConstraint(cageId: Int): Int {
        return possibleDigits.size * (grid.gridSize.width + grid.gridSize.height) + cageId
    }
}
