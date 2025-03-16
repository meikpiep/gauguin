package org.piepmeyer.gauguin.game

import org.koin.core.annotation.InjectedParam
import org.piepmeyer.gauguin.preferences.StatisticsManager

class GameSolveService(
    @InjectedParam private val game: Game,
    @InjectedParam private val statisticsManager: StatisticsManager,
) {
    fun revealSelectedCage() {
        val selectedCell = game.grid.selectedCell ?: return

        selectedCell.cage().cells.forEach { cageCell ->
            game.revealCell(cageCell)
        }

        cheatedOnGame()
    }

    fun solveGrid() {
        game.grid.cells.forEach { game.revealCell(it) }

        game.grid.selectedCell?.isSelected = false
        game.grid.selectedCell = null

        cheatedOnGame()
    }

    fun revealSelectedCell() {
        game.grid.selectedCell ?: return

        game.revealCell(game.grid.selectedCell!!)

        cheatedOnGame()
    }

    fun markInvalidChoices() {
        game.markInvalidChoices()

        cheatedOnGame()
    }

    private fun cheatedOnGame() {
        statisticsManager.storeStreak(false)
    }
}
