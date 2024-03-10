package org.piepmeyer.gauguin.game

import org.koin.core.annotation.InjectedParam

class GameSolveService(
    @InjectedParam private val game: Game,
) {
    fun revealSelectedCage(): Boolean {
        game.grid.selectedCell ?: return false

        game.grid.selectedCell?.let {
            it.isSelected = false
            it.cage().cells.forEach { cageCell -> game.revealCell(cageCell) }
        }

        return true
    }

    fun solveGrid() {
        game.grid.cells.forEach { game.revealCell(it) }

        game.grid.selectedCell?.isSelected = false
        game.grid.selectedCell = null
    }

    fun revealSelectedCell(): Boolean {
        game.grid.selectedCell ?: return false

        game.revealCell(game.grid.selectedCell!!)

        return true
    }
}
