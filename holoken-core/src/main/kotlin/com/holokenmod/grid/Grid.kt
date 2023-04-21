package com.holokenmod.grid

import com.holokenmod.options.GameOptionsVariant
import com.holokenmod.options.GameVariant

class Grid(private val variant: GameVariant) {
    var cells: List<GridCell> = mutableListOf()
    var cages: List<GridCage> = mutableListOf()
    var selectedCell: GridCell? = null
    var playTime: Long = 0
    var isActive = false
    var creationDate: Long = 0
        private set

    val possibleDigits: List<Int> by lazy {
        variant.options
            .digitSetting
            .getPossibleDigits(variant.gridSize)
    }

    constructor(variant: GameVariant, creationDate: Long) : this(variant) {
        this.creationDate = creationDate
    }

    val gridSize: GridSize
        get() = variant.gridSize

    fun getCage(row: Int, column: Int): GridCage? {
        return if (row < 0 || row >= variant.height || column < 0 || column >= variant.width) {
            null
        } else cells[column + row * variant.width].cage
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
        var counter = 0
        for (cell in cells) {
            if (cell.isCheated) {
                counter++
            }
        }
        return counter
    }

    fun numberOfMistakes(showDupedDigits: Boolean): Int {
        return cells.count { shouldBeHighlightedInvalid(it, showDupedDigits) }
    }

    private fun shouldBeHighlightedInvalid(cell: GridCell, showDupedDigits: Boolean): Boolean {
        return cell.isUserValueSet && (cell.userValue != cell.value
                || (showDupedDigits && cell.isShowWarning))
    }

    fun numberOfFilledCells(): Int = cells.count { it.isUserValueSet }

    fun getNumValueInRow(ocell: GridCell): Int {
        var count = 0
        for (cell in cells) {
            if (cell.row == ocell.row &&
                cell.userValue == ocell.userValue
            ) {
                count++
            }
        }
        return count
    }

    fun getNumValueInCol(ocell: GridCell): Int {
        var count = 0
        for (cell in cells) {
            if (cell.column == ocell.column &&
                cell.userValue == ocell.userValue
            ) {
                count++
            }
        }
        return count
    }

    fun getPossiblesInRowCol(ocell: GridCell): List<GridCell> {
        val possiblesRowCol = ArrayList<GridCell>()
        val userValue = ocell.userValue
        for (cell in cells) {
            if (cell.isPossible(userValue)) {
                if (cell.row == ocell.row || cell.column == ocell.column) {
                    possiblesRowCol.add(cell)
                }
            }
        }
        return possiblesRowCol
    }

    fun getCellAt(row: Int, column: Int): GridCell {
        if (!isValidCell(row, column))
            throw RuntimeException("invalid cell")

        return cells[column + row * variant.width]
    }

    fun isValidCell(row: Int, column: Int): Boolean {
        return row >= 0 && row < variant.height && column >= 0 && column < variant.width
    }

    fun ClearAllCages() {
        for (cell in cells) {
            cell.cage = null
            cell.setCagetext("")
        }
        cages = mutableListOf()
    }

    fun setCageTexts() {
        for (cage in cages) {
            cage.updateCageText()
        }
    }

    fun addCell(cell: GridCell) {
        cells = cells + cell
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
            it.cage!!.setSelected(false)
        }
    }

    fun clearLastModified() {
        for (cell in cells) {
            cell.isLastModified = false
        }
    }

    fun solveSelectedCage() {
        if (selectedCell == null) {
            return
        }
        for (cell in selectedCell!!.cage?.cells!!) {
            if (!cell.isUserValueCorrect) {
                cell.clearPossibles()
                cell.setUserValueIntern(cell.value)
                cell.isCheated = true
            }
        }
        selectedCell!!.isSelected = false
        selectedCell!!.cage!!.setSelected(false)
    }

    fun solveGrid() {
        for (cell in cells) {
            if (!cell.isUserValueCorrect) {
                cell.clearPossibles()
                cell.setUserValueIntern(cell.value)
                cell.isCheated = true
            }
        }
        if (selectedCell != null) {
            selectedCell!!.isSelected = false
            selectedCell!!.cage!!.setSelected(false)
        }
    }

    val maximumDigit: Int
        get() = variant.options
            .digitSetting
            .getMaximumDigit(variant.gridSize)

    val possibleNonZeroDigits: Collection<Int> by lazy {
                variant.options
                    .digitSetting
                    .getPossibleNonZeroDigits(variant.gridSize)
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
            builder.append(cell.cageText.padStart(6))
            builder.append(" ")
            val cageId = if (cell.cage != null) {
                cell.cage!!.id.toString()
            } else {
                ""
            }
            builder.append(cageId.padStart(2))
            builder.append(" ")
            if (cell.cellNumber % variant.width == variant.width - 1) {
                builder.append("|")
                builder.append(System.lineSeparator())
            }
        }
    }

    fun addAllCells() {
        var cellnum = 0
        for (row in 0 until variant.height) {
            for (column in 0 until variant.width) {
                addCell(GridCell(this, cellnum++, row, column))
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

    fun copyEmpty(): Grid {
        val grid = Grid(variant)
        grid.addAllCells()
        var cageId = 0
        for (cage in cages) {
            val newCage: GridCage = GridCage.createWithCells(
                cageId,
                grid,
                //TODO nur genutzt, um den Compilefehler zu umgehen...
                GridCageAction.ACTION_NONE,
                cage.cells
            )
            cageId++
            grid.addCage(newCage)
        }
        for (cell in cells) {
            grid.getCell(cell.cellNumber).value = cell.value
        }
        return grid
    }

    fun updateBorders() {
        for (cage in cages) {
            cage.setBorders()
        }
    }

    fun addPossiblesAtNewGame() {
        for (cell in cells) {
            addAllPossibles(cell)
        }
    }

    private fun addAllPossibles(cell: GridCell) {
        for (i in possibleDigits) {
            cell.addPossible(i)
        }
    }

    fun userValueChanged() {
        cells.filter { it.isUserValueSet }
            .forEach {
                it.isShowWarning = getNumValueInCol(it) > 1 || getNumValueInRow(it) > 1
            }

        cages.forEach { it.userValuesCorrect() }
    }

    val options: GameOptionsVariant
        get() = variant.options
}