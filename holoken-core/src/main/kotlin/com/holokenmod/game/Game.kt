package com.holokenmod.game

import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridCell
import com.holokenmod.grid.GridSolveService
import com.holokenmod.grid.GridView
import com.holokenmod.undo.UndoManager

data class Game(
    var grid: Grid,
    var undoManager: UndoManager,
    var gridUI: GridView
) {
    private var solvedListener: GameSolvedListener? = null

    private val gridSolveService = GridSolveService(grid)

    private val gridCreationListeners = mutableListOf<GridCreationListener>()

    fun addGridCreationListener(gridCreationListener: GridCreationListener) {
        gridCreationListeners += gridCreationListener
    }

    fun updateGrid(newGrid: Grid) {
        grid = newGrid
        gridUI.grid = grid

        gridCreationListeners.forEach { it.freshGridWasCreated() }
    }

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
        val selectedCell = grid.selectedCell ?: return
        if (!grid.isActive) {
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
            selectedCell.clearPossibles()
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

    private fun clearLastModified() {
        grid.clearLastModified()
        gridUI.invalidate()
    }

    fun solveSelectedCage(): Boolean {
        grid.selectedCell ?: return false
        gridSolveService.solveSelectedCage()
        gridUI.invalidate()
        return true
    }

    fun solveGrid() {
        gridSolveService.solveGrid()
        gridUI.invalidate()
    }

    fun revealSelectedCell(): Boolean {
        gridSolveService.revealSelectedCell()
        gridUI.invalidate()
        return true
    }

    fun markInvalidChoices(showDupedDigits: Boolean) {
        grid.markInvalidChoices(showDupedDigits)
        gridUI.invalidate()
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

    fun solveAllMissingCells() {
        grid.cells.forEach {
            selectCell(it)
            enterNumber(it.value, true)
        }
    }
}
