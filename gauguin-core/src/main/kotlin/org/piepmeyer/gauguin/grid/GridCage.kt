package org.piepmeyer.gauguin.grid

import org.piepmeyer.gauguin.creation.cage.GridCageType

class GridCage(
    val id: Int,
    private val showOperators: Boolean,
    val action: GridCageAction,
    val cageType: GridCageType,
) {
    var cells: List<GridCell> = mutableListOf()

    var result = 0

    private fun isAddMathsCorrect(): Boolean {
        var total = 0
        for (cell in cells) {
            total += cell.userValue
        }
        return total == result
    }

    private fun isMultiplyMathsCorrect(): Boolean {
        var total = 1
        for (cell in cells) {
            total *= cell.userValue
        }
        return total == result
    }

    fun isDivideMathsCorrect(): Boolean {
        if (cells.size != 2) {
            return false
        }

        val useValueOne = cells[0].userValue
        val useValueTwo = cells[1].userValue

        return if (result != 0) {
            if (useValueOne > useValueTwo) {
                useValueOne == useValueTwo * result
            } else {
                useValueTwo == useValueOne * result
            }
        } else {
            (useValueOne == 0).xor(useValueTwo == 0)
        }
    }

    private fun isSubtractMathsCorrect(): Boolean {
        if (cells.size != 2) {
            return false
        }
        return if (cells[0].userValue > cells[1].userValue) {
            cells[0].userValue - cells[1].userValue == result
        } else {
            cells[1].userValue - cells[0].userValue == result
        }
    }

    fun isMathsCorrect(): Boolean {
        if (cells.size == 1) {
            return cells[0].isUserValueCorrect
        }
        return if (showOperators) {
            when (action) {
                GridCageAction.ACTION_ADD -> isAddMathsCorrect()
                GridCageAction.ACTION_MULTIPLY -> isMultiplyMathsCorrect()
                GridCageAction.ACTION_DIVIDE -> isDivideMathsCorrect()
                GridCageAction.ACTION_SUBTRACT -> isSubtractMathsCorrect()
                GridCageAction.ACTION_NONE -> true
            }
        } else {
            isAddMathsCorrect() || isMultiplyMathsCorrect() ||
                isDivideMathsCorrect() || isSubtractMathsCorrect()
        }
    }

    fun isUserMathCorrect(): Boolean {
        return if (cells.any { !it.isUserValueSet }) {
            true
        } else {
            isMathsCorrect()
        }
    }

    fun addCell(cell: GridCell) {
        cells = cells + cell
        cell.cage = this
    }

    val numberOfCells: Int
        get() = cells.size

    fun getCell(cellNumber: Int): GridCell {
        return cells[cellNumber]
    }

    fun cageText(): String {
        return if (showOperators) {
            result.toString() + action.operationDisplayName
        } else {
            result.toString()
        }
    }

    fun satisfiesConstraints(possibleNumbers: IntArray): Boolean {
        return cageType.satisfiesConstraints(possibleNumbers)
    }

    companion object {
        fun createWithCells(
            id: Int,
            grid: Grid,
            action: GridCageAction,
            firstCell: GridCell,
            cageType: GridCageType,
        ): GridCage {
            val cage = GridCage(id, grid.options.showOperators, action, cageType)
            for (coordinate in cageType.coordinates) {
                val col = firstCell.column + coordinate.first
                val row = firstCell.row + coordinate.second
                cage.addCell(grid.getValidCellAt(row, col))
            }
            return cage
        }

        fun createWithSingleCellArithmetic(
            id: Int,
            grid: Grid,
            gridCell: GridCell,
        ): GridCage {
            val cage = GridCage(id, grid.options.showOperators, GridCageAction.ACTION_NONE, GridCageType.SINGLE)
            cage.result = gridCell.value
            cage.addCell(gridCell)

            return cage
        }
    }

    override fun toString(): String {
        return "GridCage id=$id"
    }
}
