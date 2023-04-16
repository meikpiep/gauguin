package com.holokenmod.grid

import com.holokenmod.Direction

class GridCell(
    private val grid: Grid, // Index of the cell (left to right, top to bottom, zero-indexed)
    val cellNumber: Int,
    val row: Int,
    val column: Int,
) {
    var value = NO_VALUE_SET
    var userValue = NO_VALUE_SET

    var cage: GridCage? = null
    var cageText = ""
        private set
    val cellBorders = GridCellBorders()
    var isCheated = false
    var possibles: Set<Int> = setOf()
    var isShowWarning = false
    var isSelected = false
    var isLastModified = false
    var isInvalidHighlight = false

    override fun toString(): String {
        return "GridCell{" +
                "mColumn=" + column +
                ", mRow=" + row +
                ", mValue=" + value +
                '}'
    }

    val isUserValueCorrect: Boolean
        get() = userValue == value
    val isUserValueSet: Boolean
        get() = userValue != NO_VALUE_SET

    /* Returns whether the cell is a member of any cage */
    fun CellInAnyCage(): Boolean {
        return cage != null
    }

    fun setCagetext(cageText: String) {
        this.cageText = cageText
    }


    fun setUserValueExtern(value: Int) {
        clearPossibles()
        userValue = value
        isInvalidHighlight = false
    }

    fun setUserValueIntern(value: Int) {
        userValue = value
    }

    @Synchronized
    fun clearUserValue() {
        userValue = NO_VALUE_SET
    }

    fun togglePossible(digit: Int) {
        possibles = if (!isPossible(digit)) {
            possibles + digit
        } else {
            possibles - digit
        }
    }

    fun isPossible(digit: Int): Boolean {
        return possibles.contains(digit)
    }

    @Synchronized
    fun removePossible(digit: Int) {
        possibles = possibles - digit
    }

    fun clearPossibles() {
        possibles = setOf()
    }

    fun addPossible(digit: Int) {
        possibles = possibles + digit
    }

    fun hasNeighbor(direction: Direction): Boolean {
        return when (direction) {
            Direction.NORTH -> grid.isValidCell(row - 1, column)
            Direction.WEST -> grid.isValidCell(row, column - 1)
            Direction.SOUTH -> grid.isValidCell(row + 1, column)
            Direction.EAST -> grid.isValidCell(row, column + 1)
        }
    }

    companion object {
        const val NO_VALUE_SET = Int.MAX_VALUE
    }
}