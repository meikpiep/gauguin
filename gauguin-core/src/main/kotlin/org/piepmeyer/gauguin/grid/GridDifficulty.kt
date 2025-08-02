package org.piepmeyer.gauguin.grid

data class GridDifficulty(
    val classicalRating: Double? = null,
    val humanDifficulty: Int? = null,
    val solvedViaHumanDifficulty: Boolean? = null,
) {
    fun humanDifficultyDisplayable(): String {
        val difficulty = humanDifficulty

        if (difficulty == null) {
            return "?"
        }

        if (!solvedViaHumanDifficulty!!) {
            return "$difficulty!"
        }

        return difficulty.toString()
    }
}
