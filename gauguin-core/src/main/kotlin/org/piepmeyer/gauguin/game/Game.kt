package org.piepmeyer.gauguin.game

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.grid.GridSolveService
import org.piepmeyer.gauguin.grid.GridView
import org.piepmeyer.gauguin.undo.UndoManager

data class Game(
    var grid: Grid,
    var undoManager: UndoManager,
    var gridUI: GridView
) {
    private var lastCellWithModifiedPossibles: GridCell? = null
    private var removePencils: Boolean = false
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

    fun enterNumber(number: Int) {
        val selectedCell = grid.selectedCell ?: return
        if (!grid.isActive) {
            return
        }
        clearLastModified()
        undoManager.saveUndo(selectedCell, false)
        selectedCell.setUserValueExtern(number)
        if (removePencils) {
            removePossibles(selectedCell)
        }

        lastCellWithModifiedPossibles = null

        if (grid.isSolved) {
            selectedCell.isSelected = false
            selectedCell.cage().setSelected(false)
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

        lastCellWithModifiedPossibles = selectedCell

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
            c.cage().setSelected(false)
        }

        cell.isSelected = true
        cell.cage().setSelected(true)

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

    fun setValueOrPossiblesOnSelectedCell(): Boolean {
        val selectedCell = grid.selectedCell ?: return false

        if (!grid.isActive) {
            return false
        }

        if (selectedCell.possibles.size == 1) {
            enterNumber(selectedCell.possibles.first())
        } else if (selectedCell.possibles.isEmpty()) {
            copyPossiblesFromLastEnteredCell(selectedCell)
        }

        return true
    }

    private fun copyPossiblesFromLastEnteredCell(selectedCell: GridCell) {
        lastCellWithModifiedPossibles?.let {
            if (it.cage() == selectedCell.cage()) {
                undoManager.saveUndo(selectedCell, false)
                selectedCell.addPossibles(it.possibles)
                gridUI.invalidate()
            }
        }
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

    fun revealSelectedCell() {
        gridSolveService.revealSelectedCell()
        gridUI.invalidate()
    }

    fun markInvalidChoices(showDupedDigits: Boolean) {
        grid.markInvalidChoices(showDupedDigits)
        gridUI.invalidate()
    }

    fun undoOneStep() {
        clearLastModified()
        undoManager.restoreUndo()
        grid.userValueChanged()

        gridUI.invalidate()
    }

    fun restartGame() {
        clearUserValues()
        grid.cells.forEach { it.clearPossibles() }
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
            enterNumber(it.value)
        }
    }

    fun setRemovePencils(removePencils: Boolean) {
        this.removePencils = removePencils
    }
}
