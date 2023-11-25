package org.piepmeyer.gauguin.game

import org.piepmeyer.gauguin.grid.GridCell

class FastFinishingGameMode(
    private val game: Game
) : GameMode {

    override fun isFastFinishingMode() = true

    override fun cellClicked(cell: GridCell) {
        if (cell.possibles.size == 1) {
            game.enterNumber(cell.possibles.first())
        }
    }

    override fun cellLongClicked(cell: GridCell) {
        cellClicked(cell)
    }

    override fun enterPossibleNumber(cell: GridCell, number: Int) {
        game.enterPossibleNumberCore(cell, number)
    }
}
