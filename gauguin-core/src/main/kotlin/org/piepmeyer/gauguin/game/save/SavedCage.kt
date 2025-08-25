package org.piepmeyer.gauguin.game.save

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCageAction

@Serializable
data class SavedCage(
    val id: Int,
    val action: GridCageAction,
    val type: GridCageType,
    val result: Int,
    val cellNumbers: List<Int>,
) {
    companion object {
        fun fromCage(cage: GridCage): SavedCage =
            SavedCage(
                id = cage.id,
                action = cage.action,
                type = cage.cageType,
                result = cage.result,
                cellNumbers = cage.cells.map { it.cellNumber },
            )
    }
}
