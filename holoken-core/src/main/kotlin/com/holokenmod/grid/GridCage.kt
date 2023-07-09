package com.holokenmod.grid

class GridCage(
    val id: Int,
    private val grid: Grid,
    val action: GridCageAction
) {
    var cells: List<GridCell> = mutableListOf()

    var cageText: String = ""
        private set

    var result = 0
    private var mUserMathCorrect = true
    private var mSelected = false

    override fun toString(): String {
        var retStr = ""
        retStr += "Cage id: $id"
        retStr += ", Action: "
        retStr += when (action) {
            GridCageAction.ACTION_NONE -> "None"
            GridCageAction.ACTION_ADD -> "Add"
            GridCageAction.ACTION_SUBTRACT -> "Subtract"
            GridCageAction.ACTION_MULTIPLY -> "Multiply"
            GridCageAction.ACTION_DIVIDE -> "Divide"
        }
        retStr += ", ActionStr: " + action.operationDisplayName + ", Result: " + result
        retStr += ", cells: $cellNumbers"
        return retStr
    }

    private val isAddMathsCorrect: Boolean
        get() {
            var total = 0
            for (cell in cells) {
                total += cell.userValue
            }
            return total == result
        }
    private val isMultiplyMathsCorrect: Boolean
        get() {
            var total = 1
            for (cell in cells) {
                total *= cell.userValue
            }
            return total == result
        }
    private val isDivideMathsCorrect: Boolean
        get() {
            if (cells.size != 2) {
                return false
            }
            return if (cells[0].userValue > cells[1].userValue) {
                cells[0].userValue == cells[1].userValue * result
            } else {
                cells[1].userValue == cells[0].userValue * result
            }
        }
    private val isSubtractMathsCorrect: Boolean
        get() {
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
        return if (grid.options.showOperators) {
            when (action) {
                GridCageAction.ACTION_ADD -> isAddMathsCorrect
                GridCageAction.ACTION_MULTIPLY -> isMultiplyMathsCorrect
                GridCageAction.ACTION_DIVIDE -> isDivideMathsCorrect
                GridCageAction.ACTION_SUBTRACT -> isSubtractMathsCorrect
                GridCageAction.ACTION_NONE -> true
            }
        } else {
            isAddMathsCorrect || isMultiplyMathsCorrect ||
                isDivideMathsCorrect || isSubtractMathsCorrect
        }
    }

    fun userValuesCorrect() {
        mUserMathCorrect = true

        for (cell in cells) {
            if (!cell.isUserValueSet) {
                return
            }
        }
        mUserMathCorrect = isMathsCorrect()
    }

    fun setBorders() {
        for (cell in cells) {
            cell.cellBorders.resetBorders()

            val borderType = when {
                !mUserMathCorrect && grid.options.showBadMaths -> GridBorderType.BORDER_WARN
                mSelected -> GridBorderType.BORDER_CAGE_SELECTED
                else -> GridBorderType.BORDER_SOLID
            }

            if (grid.getCage(cell.row - 1, cell.column) != this) {
                cell.cellBorders.north = borderType
            }

            if (grid.getCage(cell.row, cell.column + 1) != this) {
                cell.cellBorders.east = borderType
            }

            if (grid.getCage(cell.row + 1, cell.column) != this) {
                cell.cellBorders.south = borderType
            }

            if (grid.getCage(cell.row, cell.column - 1) != this) {
                cell.cellBorders.west = borderType
            }
        }
    }

    fun addCell(cell: GridCell) {
        cells = cells + cell
        cell.cage = this
    }

    val cellNumbers: String
        get() {
            val numbers = StringBuilder()
            for (cell in cells) {
                numbers.append(cell.cellNumber).append(",")
            }
            return numbers.toString()
        }
    val numberOfCells: Int
        get() = cells.size

    fun getCell(cellNumber: Int): GridCell {
        return cells[cellNumber]
    }

    fun updateCageText() {
        cageText = if (grid.options.showOperators) {
            result.toString() + action.operationDisplayName
        } else {
            result.toString()
        }
    }

    fun setSelected(mSelected: Boolean) {
        this.mSelected = mSelected
    }

    fun calculateResultFromAction() {
        if (action == GridCageAction.ACTION_ADD) {
            var total = 0
            for (cell in cells) {
                total += cell.value
            }
            result = total
            return
        }
        if (action == GridCageAction.ACTION_MULTIPLY) {
            var total = 1
            for (cell in cells) {
                total *= cell.value
            }
            result = total
            return
        }
        val cell1Value = cells[0].value
        val cell2Value = cells[1].value
        var higher = cell1Value
        var lower = cell2Value
        if (cell1Value < cell2Value) {
            higher = cell2Value
            lower = cell1Value
        }
        if (action == GridCageAction.ACTION_DIVIDE) {
            if (lower == 0) {
                result = 0
                return
            }
            result = higher / lower
        } else {
            result = higher - lower
        }
    }

    companion object {
        fun createWithCells(
            id: Int,
            grid: Grid,
            action: GridCageAction,
            firstCell: GridCell,
            cage_coords: Array<Pair<Int, Int>>
        ): GridCage {
            val cage = GridCage(id, grid, action)
            for (cage_coord in cage_coords) {
                val col = firstCell.column + cage_coord.first
                val row = firstCell.row + cage_coord.second
                cage.addCell(grid.getValidCellAt(row, col))
            }
            return cage
        }

        fun createWithCells(
            id: Int,
            grid: Grid,
            action: GridCageAction,
            cells: Collection<GridCell>
        ): GridCage {
            val cage = GridCage(id, grid, action)
            for (cell in cells) {
                cage.addCell(grid.getCell(cell.cellNumber))
            }
            return cage
        }

        fun createWithSingleCellArithmetic(id: Int, grid: Grid, gridCell: GridCell): GridCage {
            val cage = GridCage(id, grid, GridCageAction.ACTION_NONE)
            cage.result = gridCell.value
            cage.addCell(gridCell)

            return cage
        }
    }
}
