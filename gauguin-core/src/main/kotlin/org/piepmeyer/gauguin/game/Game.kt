package org.piepmeyer.gauguin.game

import org.koin.core.annotation.InjectedParam
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.grid.GridView
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.preferences.StatisticsManager
import org.piepmeyer.gauguin.undo.UndoManager

data class Game(
    var grid: Grid,
    var undoManager: UndoManager,
    var gridUI: GridView,
    @InjectedParam private val statisticsManager: StatisticsManager,
    @InjectedParam private val applicationPreferences: ApplicationPreferences,
) {
    private var lastCellWithModifiedPossibles: GridCell? = null
    private var solvedListeners = mutableListOf<GameSolvedListener>()

    private val gridCreationListeners = mutableListOf<GridCreationListener>()

    private var gameMode: GameMode = RegularGameMode(this, applicationPreferences)
    private val gameModeListeners = mutableListOf<GameModeListener>()

    fun enterFastFinishingMode() {
        gameMode = FastFinishingGameMode(this)
        gameModeListeners.forEach { it.changedGameMode() }

        gridUI.invalidate()
    }

    fun exitFastFinishingMode() {
        gameMode = RegularGameMode(this, applicationPreferences)
        gameModeListeners.forEach { it.changedGameMode() }

        gridUI.invalidate()
    }

    fun addGridCreationListener(gridCreationListener: GridCreationListener) {
        gridCreationListeners += gridCreationListener
    }

    fun updateGrid(newGrid: Grid) {
        grid = newGrid
        gridUI.grid = grid

        ensureNotInFastFinishingMode()

        grid.updateDuplicatedNumbersInRowOrColumn()

        gridCreationListeners.forEach { it.freshGridWasCreated() }
    }

    fun enterNumber(
        number: Int,
        reveal: Boolean = false,
    ) {
        val selectedCell = grid.selectedCell ?: return
        if (!grid.isActive) {
            return
        }

        gridHasBeenPlayed()

        clearLastModified()
        undoManager.saveUndo(selectedCell, false)
        selectedCell.setUserValueExtern(number)

        if (applicationPreferences.removePencils()) {
            removePossibles(selectedCell)
        }

        lastCellWithModifiedPossibles = null

        if (grid.isSolved()) {
            selectedCell.isSelected = false
            grid.isActive = false

            ensureNotInFastFinishingMode()

            if (!reveal) {
                statisticsManager.puzzleSolved(grid)
                statisticsManager.storeStatisticsAfterFinishedGame(grid)
            }

            solvedListeners.forEach { it.puzzleSolved(reveal) }
        }

        grid.userValueChanged()

        gridUI.requestFocus()
        gridUI.invalidate()
    }

    fun revealCell(cell: GridCell) {
        if (!cell.isUserValueCorrect) {
            selectCell(cell)
            cell.isCheated = true
            enterNumber(cell.value, reveal = true)
        }
    }

    private fun ensureNotInFastFinishingMode() {
        if (isInFastFinishingMode()) {
            exitFastFinishingMode()
        }
    }

    fun addGameSolvedHandler(listener: GameSolvedListener) {
        solvedListeners.add(listener)
    }

    fun enterPossibleNumber(number: Int) {
        val selectedCell = grid.selectedCell ?: return
        if (!grid.isActive) {
            return
        }

        gridHasBeenPlayed()

        gameMode.enterPossibleNumber(selectedCell, number)

        gridUI.requestFocus()
        gridUI.invalidate()
    }

    fun enterPossibleNumberCore(
        selectedCell: GridCell,
        number: Int,
    ) {
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
    }

    private fun gridHasBeenPlayed() {
        if (!grid.startedToBePlayed) {
            grid.startedToBePlayed = true

            statisticsManager.puzzleStartedToBePlayed()
        }
    }

    private fun removePossibles(
        selectedCell: GridCell,
        saveUndo: Boolean = true,
    ) {
        val possibleCells = grid.getPossiblesInRowCol(selectedCell)
        for (cell in possibleCells) {
            if (saveUndo) {
                undoManager.saveUndo(cell, true)
                cell.isLastModified = true
            }
            cell.removePossible(selectedCell.userValue)
        }
    }

    fun cellClicked(cell: GridCell) {
        selectCell(cell)

        gameMode.cellClicked(cell)

        gridUI.requestFocus()
        gridUI.invalidate()
    }

    fun selectCell(cell: GridCell) {
        grid.selectedCell = cell

        grid.cells.forEach { it.isSelected = false }

        cell.isSelected = true
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

    fun longClickOnSelectedCell(): Boolean {
        val selectedCell = grid.selectedCell ?: return false

        if (!grid.isActive) {
            return false
        }

        gameMode.cellLongClicked(selectedCell)

        return true
    }

    fun copyPossiblesFromLastEnteredCell(selectedCell: GridCell) {
        lastCellWithModifiedPossibles?.let {
            if (it.cage() == selectedCell.cage()) {
                undoManager.saveUndo(selectedCell, false)
                selectedCell.possibles = it.possibles
                gridUI.invalidate()
            }
        }
    }

    private fun clearUserValues() {
        ensureNotInFastFinishingMode()

        grid.clearUserValues()
        gridUI.invalidate()
    }

    private fun clearLastModified() {
        grid.clearLastModified()
        gridUI.invalidate()
    }

    fun markInvalidChoices() {
        grid.markInvalidChoices()
        gridUI.invalidate()
    }

    fun undoOneStep() {
        ensureNotInFastFinishingMode()

        clearLastModified()
        undoManager.restoreUndo()
        grid.userValueChanged()

        gridUI.invalidate()
    }

    fun restartGame() {
        clearUserValues()

        grid.markInvalidChoices()
        grid.updateDuplicatedNumbersInRowOrColumn()
        grid.cells.forEach {
            it.clearPossibles()
            it.isLastModified = false
        }
        grid.selectedCell = null
        grid.isActive = true
    }

    fun clearUndoList() {
        undoManager.clear()
    }

    fun solveAllMissingCells() {
        grid.cells.forEach {
            cellClicked(it)
            enterNumber(it.value)
        }
    }

    fun addGameModeListener(listener: GameModeListener) {
        gameModeListeners += listener
    }

    fun isInFastFinishingMode(): Boolean = gameMode.isFastFinishingMode()

    fun fillSingleCagesInNewGrid() {
        grid.cages.filter { it.cageType == GridCageType.SINGLE }
            .forEach {
                val onlyCell = it.getCell(0)

                onlyCell.setUserValueIntern(onlyCell.value)
                onlyCell.clearPossibles()

                if (applicationPreferences.removePencils()) {
                    removePossibles(onlyCell, false)
                }
            }
    }
}
