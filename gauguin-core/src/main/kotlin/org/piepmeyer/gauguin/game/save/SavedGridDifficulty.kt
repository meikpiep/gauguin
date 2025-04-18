package org.piepmeyer.gauguin.game.save

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.grid.GridDifficulty

@Serializable
data class SavedGridDifficulty(
    val classicalRating: Double? = null,
    val humanDifficulty: Int? = null,
    val solvedViaHumanDifficulty: Boolean? = null,
) {
    fun toDifficulty(): GridDifficulty = GridDifficulty(classicalRating, humanDifficulty, solvedViaHumanDifficulty)

    companion object {
        fun fromDifficulty(difficulty: GridDifficulty): SavedGridDifficulty =
            SavedGridDifficulty(difficulty.classicalRating, difficulty.humanDifficulty, difficulty.solvedViaHumanDifficulty)
    }
}
