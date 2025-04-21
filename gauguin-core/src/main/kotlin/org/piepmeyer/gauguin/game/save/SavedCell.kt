package org.piepmeyer.gauguin.game.save

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.grid.GridCell

@Serializable
data class SavedCell(
    val cellNumber: Int,
    val row: Int,
    val column: Int,
    val value: Int,
    val userValue: Int?,
    val possibles: Set<Int>,
) {
    companion object {
        fun fromCell(cell: GridCell): SavedCell =
            SavedCell(
                cellNumber = cell.cellNumber,
                row = cell.row,
                column = cell.column,
                value = cell.value,
                userValue = cell.userValue,
                possibles = cell.possibles,
            )
    }
}
