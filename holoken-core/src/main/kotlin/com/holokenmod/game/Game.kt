package com.holokenmod.game

import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridCell
import com.holokenmod.grid.GridView
import com.holokenmod.undo.UndoManager

data class Game(
    var grid: Grid,
    var undoManager: UndoManager,
    var gridUI: GridView
) {
    private var solvedListener: GameSolvedListener? = null

    @Synchronized
    fun enterNumber(number: Int, removePossibles: Boolean) {
        val selectedCell = grid.selectedCell
        if (!grid.isActive) {
            return
        }
        if (selectedCell == null) {
            return
        }
        clearLastModified()
        undoManager.saveUndo(selectedCell, false)
        selectedCell.setUserValueExtern(number)
        if (removePossibles) {
            removePossibles(selectedCell)
        }
        if (grid.isActive && grid.isSolved) {
            selectedCell.isSelected = false
            selectedCell.cage?.setSelected(false)
            grid.isActive = false
            solvedListener?.puzzleSolved()
        }

        grid.userValueChanged()

        gridUI.requestFocus()
        gridUI.invalidate()
    }

    fun setSolvedHandler(listener: GameSolvedListener?) {
        solvedListener = listener
    }

    @Synchronized
    fun enterPossibleNumber(number: Int) {
        val selectedCell = grid.selectedCell
        if (!grid.isActive) {
            return
        }
        if (selectedCell == null) {
            return
        }
        clearLastModified()
        undoManager.saveUndo(selectedCell, false)
        if (selectedCell.isUserValueSet) {
            val oldValue = selectedCell.userValue
            selectedCell.clearUserValue()
            selectedCell.togglePossible(oldValue)
            grid.userValueChanged()
        }
        selectedCell.togglePossible(number)
        gridUI.requestFocus()
        gridUI.invalidate()
    }

    private fun removePossibles(selectedCell: GridCell) {
        val possibleCells = grid.getPossiblesInRowCol(selectedCell)
        for (cell in possibleCells) {
            undoManager.saveUndo(cell, true)
            cell.isLastModified = true
            cell.removePossible(selectedCell.userValue)
        }
    }

    fun selectCell(cell: GridCell) {
        grid.selectedCell = cell

        for (c in grid.cells) {
            c.isSelected = false
            c.cage?.setSelected(false)
        }

        cell.isSelected = true
        cell.cage?.setSelected(true)

        gridUI.requestFocus()
        gridUI.invalidate()
    }

    fun eraseSelectedCell() {
        val selectedCell = grid.selectedCell ?: return

        if (!grid.isActive) {
            return
        }

        if (selectedCell.isUserValueSet || selectedCell.possibles.isNotEmpty()) {
            clearLastModified()
            undoManager.saveUndo(selectedCell, false)
            selectedCell.clearUserValue()
            grid.userValueChanged()
        }
    }

    fun setSinglePossibleOnSelectedCell(removePossibles: Boolean): Boolean {
        val selectedCell = grid.selectedCell ?: return false

        if (!grid.isActive) {
            return false
        }

        if (selectedCell.possibles.size == 1) {
            enterNumber(selectedCell.possibles.first(), removePossibles)
        }

        return true
    }

    private fun clearUserValues() {
        grid.clearUserValues()
        gridUI.invalidate()
    }

    fun clearLastModified() {
        grid.clearLastModified()
        gridUI.invalidate()
    }

    fun solveSelectedCage(): Boolean {
        grid.selectedCell ?: return false
        grid.solveSelectedCage()
        gridUI.invalidate()
        return true
    }

    fun solveGrid() {
        grid.solveGrid()
        gridUI.invalidate()
    }

    fun markInvalidChoices(showDupedDigits: Boolean) {
        grid.markInvalidChoices(showDupedDigits)
        gridUI.invalidate()
    }

    fun revealSelectedCell(): Boolean {
        val selectedCell = grid.selectedCell ?: return false
        selectedCell.setUserValueIntern(selectedCell.value)
        selectedCell.isCheated = true
        gridUI.invalidate()
        return true
    }

    fun undoOneStep() {
        clearLastModified()
        undoManager.restoreUndo()
        gridUI.invalidate()
    }

    fun restartGame() {
        clearUserValues()
        grid.isActive = true
    }

    fun restoreUndo() {
        undoManager.restoreUndo()
    }

    fun clearUndoList() {
        undoManager.clear()
    }

    fun undo() {
        clearLastModified()
        restoreUndo()
        gridUI.invalidate()
    }

    fun enterAllMissingCells() {
        grid.cells.forEach {
            selectCell(it)
            enterNumber(it.value, true)
        }
    }
}