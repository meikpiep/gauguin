package org.piepmeyer.gauguin.game.save

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.undo.UndoStep

@Serializable
data class SavedUndoStep(
    val cellNumber: Int,
    val userValue: Int,
    val possibles: Set<Int>,
    val isBatch: Boolean,
) {
    fun toUndoStep(grid: Grid) =
        UndoStep(
            cell = grid.cells[cellNumber],
            userValue = userValue,
            possibles = possibles,
            isBatch = isBatch,
        )

    companion object {
        fun fromUndoStep(undoStep: UndoStep) =
            SavedUndoStep(
                cellNumber = undoStep.cell.cellNumber,
                userValue = undoStep.userValue,
                possibles = undoStep.possibles,
                isBatch = undoStep.isBatch,
            )
    }
}
