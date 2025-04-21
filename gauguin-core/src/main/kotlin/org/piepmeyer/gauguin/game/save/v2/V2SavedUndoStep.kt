package org.piepmeyer.gauguin.game.save.v2

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.undo.UndoStep

@Serializable
data class V2SavedUndoStep(
    val cellNumber: Int,
    val userValue: Int?,
    val possibles: Set<Int>,
    val isBatch: Boolean,
) {
    fun toUndoStep(grid: Grid) =
        UndoStep(
            cell = grid.cells[cellNumber],
            userValue = if (userValue == GridCell.NO_VALUE_SET) null else userValue,
            possibles = possibles,
            isBatch = isBatch,
        )
}
