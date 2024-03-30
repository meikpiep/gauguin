package org.piepmeyer.gauguin.game

import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

class RegularGameMode(
    private val game: Game,
    private val applicationPreferences: ApplicationPreferences,
) : GameMode {
    private var filledSinglePossibleInLine = 0

    override fun isFastFinishingMode() = false

    override fun cellClicked(cell: GridCell) {
        game.selectCell(cell)
    }

    override fun cellLongClicked(cell: GridCell) {
        if (cell.possibles.size == 1) {
            game.enterNumber(cell.possibles.first())

            if (applicationPreferences.useFastFinishingMode) {
                filledSinglePossibleInLine++

                if (filledSinglePossibleInLine >= 3 && game.grid.hasCellsWithSinglePossibles()) {
                    game.enterFastFinishingMode()
                }
            }
        } else if (cell.possibles.isEmpty()) {
            filledSinglePossibleInLine = 0
            game.copyPossiblesFromLastEnteredCell(cell)
        }
    }

    override fun enterPossibleNumber(
        cell: GridCell,
        number: Int,
    ) {
        filledSinglePossibleInLine = 0
        game.enterPossibleNumberCore(cell, number)
    }
}
