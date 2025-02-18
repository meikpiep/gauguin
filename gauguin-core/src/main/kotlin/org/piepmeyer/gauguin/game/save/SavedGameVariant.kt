package org.piepmeyer.gauguin.game.save

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameVariant

@Serializable
data class SavedGameVariant(
    val gridSize: GridSize,
    val options: SavedGameOptionsVariant,
) {
    fun toVariant(): GameVariant = GameVariant(gridSize, options.toOptionsVariant())

    companion object {
        fun fromVariant(variant: GameVariant): SavedGameVariant =
            SavedGameVariant(variant.gridSize, SavedGameOptionsVariant.fromOptionsVariant(variant.options))
    }
}
