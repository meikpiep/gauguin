package com.holokenmod.game

import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridCell
import com.holokenmod.grid.GridView
import com.holokenmod.undo.UndoManager

data class Game(
    val grid: Grid,
    private val undoManager: UndoManager,
    val gridUI: GridView
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
            grid.selectedCell?.let {
                it.isSelected = false
                it.cage!!.setSelected(false)
            }
            grid.isActive = false
            if (solvedListener != null) {
                solvedListener!!.puzzleSolved()
            }
        }
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

    fun selectCell() {
        val selectedCell = grid.selectedCell
        if (!grid.isActive) {
            return
        }
        if (selectedCell == null) {
            return
        }
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

    fun markInvalidChoices() {
        grid.markInvalidChoices()
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
}