package com.holokenmod.grid

import com.holokenmod.options.GameOptionsVariant
import com.holokenmod.options.GameVariant
import kotlin.time.Duration

class Grid(
    val variant: GameVariant
) {
    val cells = createCells()
    var cages: List<GridCage> = mutableListOf()
    var selectedCell: GridCell? = null
    var playTime: Duration = Duration.ZERO
    var isActive = false
    var creationDate: Long = 0
        private set

    constructor(variant: GameVariant, creationDate: Long) : this(variant) {
        this.creationDate = creationDate
    }

    private fun createCells(): List<GridCell> {
        val listOfCells = mutableListOf<GridCell>()

        var cellnum = 0
        for (row in 0 until variant.height) {
            for (column in 0 until variant.width) {
                listOfCells += GridCell(cellnum++, row, column)
            }
        }

        return listOfCells.toList()
    }

    val gridSize: GridSize
        get() = variant.gridSize

    fun getCage(row: Int, column: Int): GridCage? {
        return if (!isValidCell(row, column)) {
            null
        } else {
            cells[column + row * variant.width].cage
        }
    }

    fun invalidsHighlighted(): List<GridCell> {
        return cells.filter {
            it.isInvalidHighlight
        }
    }

    fun cheatedHighlighted(): List<GridCell> {
        return cells.filter {
            it.isCheated
        }
    }

    fun markInvalidChoices(showDupedDigits: Boolean) {
        for (cell in cells) {
            if (shouldBeHighlightedInvalid(cell, showDupedDigits)) {
                cell.isInvalidHighlight = true
            }
        }
    }

    val isSolved: Boolean
        get() {
            for (cell in cells) {
                if (!cell.isUserValueCorrect) {
                    return false
                }
            }
            return true
        }

    fun countCheated(): Int {
        return cells.count { it.isCheated }
    }

    fun numberOfMistakes(showDupedDigits: Boolean): Int {
        return cells.count { shouldBeHighlightedInvalid(it, showDupedDigits) }
    }

    private fun shouldBeHighlightedInvalid(cell: GridCell, showDupedDigits: Boolean): Boolean {
        return cell.isUserValueSet && (
            cell.userValue != cell.value ||
                (showDupedDigits && cell.duplicatedInRowOrColumn)
            )
    }

    fun numberOfFilledCells(): Int = cells.count { it.isUserValueSet }

    private fun getNumValueInRow(ocell: GridCell): Int {
        return cells.count {
            it.row == ocell.row &&
                it.userValue == ocell.userValue
        }
    }

    private fun getNumValueInCol(ocell: GridCell): Int {
        return cells.count {
            it.column == ocell.column &&
                it.userValue == ocell.userValue
        }
    }

    fun getPossiblesInRowCol(cell: GridCell): List<GridCell> {
        return cells.filter { it.isPossible(cell.userValue) }
            .filter { it.row == cell.row || it.column == cell.column }
    }

    fun getCellAt(row: Int, column: Int): GridCell? {
        if (!isValidCell(row, column)) {
            return null
        }

        return getValidCellAt(row, column)
    }

    fun getValidCellAt(row: Int, column: Int): GridCell {
        return cells[column + row * variant.width]
    }

    private fun isValidCell(row: Int, column: Int): Boolean {
        return row >= 0 && row < variant.height && column >= 0 && column < variant.width
    }

    fun clearAllCages() {
        for (cell in cells) {
            cell.cage = null
        }
        cages = mutableListOf()
    }

    fun setCageTexts() {
        for (cage in cages) {
            cage.updateCageText()
        }
    }

    fun getCell(index: Int): GridCell {
        return cells[index]
    }

    fun addCage(cage: GridCage) {
        cages = cages + cage
    }

    fun clearUserValues() {
        for (cell in cells) {
            cell.clearUserValue()
            cell.isCheated = false
        }

        selectedCell?.let {
            it.isSelected = false
            it.cage().setSelected(false)
        }
    }

    fun clearLastModified() {
        for (cell in cells) {
            cell.isLastModified = false
        }
    }

    override fun toString(): String {
        val builder = StringBuilder("Grid:" + System.lineSeparator())
        toStringOfCellValues(builder)
        builder.append(System.lineSeparator())
        builder.append(System.lineSeparator())
        toStringOfCages(builder)
        return builder.toString()
    }

    private fun toStringOfCellValues(builder: StringBuilder) {
        for (cell in cells) {
            val userValue =
                if (cell.userValue == GridCell.NO_VALUE_SET) "-" else cell.userValue.toString()
            val value =
                if (cell.value == GridCell.NO_VALUE_SET) "-" else cell.value.toString()
            builder.append("| ")
                .append(userValue.padStart(2))
                .append(" ")
                .append(value.padStart(2))
                .append(" ")
            if (cell.cellNumber % variant.width == variant.width - 1) {
                builder.append("|")
                builder.append(System.lineSeparator())
            }
        }
    }

    private fun toStringOfCages(builder: StringBuilder) {
        for (cell in cells) {
            builder.append("| ")

            val cageText = if (cell.cage?.cells?.first() == cell) {
                cell.cage().cageText
            } else {
                ""
            }

            builder.append(cageText.padStart(6))

            builder.append(" ")

            builder.append(cell.cage?.id.toString().padStart(2))
            builder.append(" ")
            if (cell.cellNumber % variant.width == variant.width - 1) {
                builder.append("|")
                builder.append(System.lineSeparator())
            }
        }
    }

    fun isUserValueUsedInSameRow(cellIndex: Int, value: Int): Boolean {
        val startIndex = cellIndex - cellIndex % variant.width
        for (index in startIndex until startIndex + variant.width) {
            if (index != cellIndex && cells[index].userValue == value) {
                return true
            }
        }
        return false
    }

    fun isUserValueUsedInSameColumn(cellIndex: Int, value: Int): Boolean {
        var index = cellIndex % variant.width
        while (index < variant.surfaceArea) {
            if (index != cellIndex && cells[index].userValue == value) {
                return true
            }
            index += variant.width
        }
        return false
    }

    fun isValueUsedInSameRow(cellIndex: Int, value: Int): Boolean {
        val startIndex = cellIndex - cellIndex % variant.width
        for (index in startIndex until startIndex + variant.width) {
            if (index != cellIndex && cells[index].value == value) {
                return true
            }
        }
        return false
    }

    fun isValueUsedInSameColumn(cellIndex: Int, value: Int): Boolean {
        var index = cellIndex % variant.width
        while (index < variant.surfaceArea) {
            if (index != cellIndex && cells[index].value == value) {
                return true
            }
            index += variant.width
        }
        return false
    }

    fun addPossiblesAtNewGame() {
        for (cell in cells) {
            cell.addPossibles(variant.possibleDigits)
        }
    }

    fun userValueChanged() {
        cells.forEach {
            it.duplicatedInRowOrColumn = it.isUserValueSet && (getNumValueInCol(it) > 1 || getNumValueInRow(it) > 1)
        }

        cages.forEach { it.userValuesCorrect() }
    }

    val options: GameOptionsVariant
        get() = variant.options
}
