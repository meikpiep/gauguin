package org.piepmeyer.gauguin.game

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GameSolveService : KoinComponent {
    private val game: Game by inject()

    fun revealSelectedCage(): Boolean {
        game.grid.selectedCell ?: return false

        game.grid.selectedCell?.let {
            it.isSelected = false
            it.cage().cells.forEach { game.revealCell(it) }
        }

        return true
    }

    fun solveGrid() {
        game.grid.cells.forEach { game.revealCell(it) }

        game.grid.selectedCell?.let {
            it.isSelected = false
        }
    }

    fun revealSelectedCell(): Boolean {
        game.grid.selectedCell ?: return false

        game.revealCell(game.grid.selectedCell!!)

        return true
    }
}
