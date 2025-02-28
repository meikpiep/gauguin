package org.piepmeyer.gauguin.game.save.v1

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.game.save.SavedGameVariant
import org.piepmeyer.gauguin.grid.GridSize

@Serializable
data class V1SavedGameVariant(
    val gridSize: GridSize,
    val options: V1SavedGameOptionsVariant,
) {
    fun toUpdatedSavedGameVariant(): SavedGameVariant = SavedGameVariant(gridSize, options.toSavedGameOptionsVariant())
}
