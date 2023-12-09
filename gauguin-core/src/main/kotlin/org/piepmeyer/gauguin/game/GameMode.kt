package org.piepmeyer.gauguin.game

import org.piepmeyer.gauguin.grid.GridCell

interface GameMode {
    fun cellClicked(cell: GridCell)

    fun cellLongClicked(cell: GridCell)

    fun enterPossibleNumber(
        cell: GridCell,
        number: Int,
    )

    fun isFastFinishingMode(): Boolean
}
