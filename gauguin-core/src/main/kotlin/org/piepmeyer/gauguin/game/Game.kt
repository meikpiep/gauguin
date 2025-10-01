package org.piepmeyer.gauguin.game

import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.InjectedParam
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.grid.GridView
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.preferences.StatisticsManagerWriting
import org.piepmeyer.gauguin.undo.UndoManager
import org.piepmeyer.gauguin.undo.UndoManagerImpl

private val logger = KotlinLogging.logger {}

data class Game(
    val initalGrid: Grid,
    var gridUI: GridView,
    @InjectedParam private val statisticsManager: StatisticsManagerWriting,
    @InjectedParam private val applicationPreferences: ApplicationPreferences,
) {
    private var vipSolvedListeners = mutableListOf<GameSolvedListener>()
    private var solvedListeners = mutableListOf<GameSolvedListener>()

    private val gridCreationListeners = mutableListOf<GridCreationListener>()

    private var gameMode: GameMode = RegularGameMode(this, applicationPreferences)
    private val gameModeListeners = mutableListOf<GameModeListener>()

    var grid: Grid = initalGrid
        private set

    val undoManager: UndoManager = UndoManagerImpl { grid }

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

    fun removeGridCreationListener(gridCreationListener: GridCreationListener) {
        gridCreationListeners -= gridCreationListener
    }

    fun updateGrid(newGrid: Grid) {
        logger.info { "Updating grid, old grid: ${grid.detailedToString()}" }
        logger.info { "Updating grid, new grid: ${newGrid.detailedToString()}" }
        grid = newGrid
        gridUI.grid = grid

        ensureNotInFastFinishingMode()

        grid.updateDuplicatedNumbersInRowOrColumn()

        gridCreationListeners.forEach { it.freshGridWasCreated() }
        logger.info { "Updated grid to: ${grid.detailedToString()}" }
    }

    fun enterNumber(
        number: Int,
        cell: GridCell? = null,
    ) {
        val cellToEnterNumber = cell ?: grid.selectedCell ?: return

        if (!grid.isActive) {
            return
        }

        gridHasBeenPlayed()

        clearLastModified()
        undoManager.saveUndo(cellToEnterNumber, false)
        cellToEnterNumber.setUserValueExtern(number)

        if (applicationPreferences.removePencils()) {
            removePossibles(cellToEnterNumber)
        }

        if (grid.isSolved()) {
            cellToEnterNumber.isSelected = false
            grid.isActive = false

            ensureNotInFastFinishingMode()

            val cheated = grid.isCheated()

            if (!cheated) {
                statisticsManager.puzzleSolved(grid)
                statisticsManager.storeStatisticsAfterFinishedGame(grid)
            }

            statisticsManager.storeStreak(!cheated)

            vipSolvedListeners.forEach { it.puzzleSolved() }
            solvedListeners.forEach { it.puzzleSolved() }
        }

        grid.userValueChanged()

        gridUI.requestFocus()
        gridUI.invalidate()
    }

    fun revealCell(cell: GridCell) {
        cell.isCheated = true

        if (!cell.isUserValueCorrect) {
            enterNumber(cell.value, cell)
        }
    }

    private fun ensureNotInFastFinishingMode() {
        if (isInFastFinishingMode()) {
            exitFastFinishingMode()
        }
    }

    fun addGameVipSolvedHandler(listener: GameSolvedListener) {
        vipSolvedListeners.add(listener)
    }

    fun addGameSolvedHandler(listener: GameSolvedListener) {
        solvedListeners.add(listener)
    }

    fun removeGameSolvedHandler(listener: GameSolvedListener) {
        solvedListeners.remove(listener)
    }

    fun enterPossibleNumber(number: Int) {
        val selectedCell = grid.selectedCell ?: return
        if (!grid.isActive || selectedCell.isCheated) {
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
        if (grid.selectedCell == cell) {
            return
        }

        grid.selectedCell = cell

        grid.cells.forEach { it.isSelected = false }

        cell.isSelected = true
    }

    fun eraseSelectedCell() {
        val selectedCell = grid.selectedCell ?: return

        if (!grid.isActive || selectedCell.isCheated) {
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

        if (!grid.isActive || selectedCell.isCheated) {
            return false
        }

        gameMode.cellLongClicked(selectedCell)

        return true
    }

    fun copyPossiblesFromLastEnteredCell(selectedCell: GridCell) {
        val possibles = selectedCell.possiblesToBeFilled()

        if (possibles.isNotEmpty()) {
            undoManager.saveUndo(selectedCell, false)
            selectedCell.possibles = possibles
            gridUI.invalidate()
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

    fun removeGameModeListener(listener: GameModeListener) {
        gameModeListeners -= listener
    }

    fun isInFastFinishingMode(): Boolean = gameMode.isFastFinishingMode()

    fun fillSingleCagesInNewGrid() {
        grid.cages
            .filter { it.cageType == GridCageType.SINGLE }
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
