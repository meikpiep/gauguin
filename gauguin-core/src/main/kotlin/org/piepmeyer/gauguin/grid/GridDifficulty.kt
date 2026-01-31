package org.piepmeyer.gauguin.grid

data class GridDifficulty(
    val classicalRating: Double? = null,
    val humanDifficulty: Int? = null,
    val solvedViaHumanDifficulty: Boolean? = null,
    val solvedViaHumanDifficultyIncludingNishio: Boolean? = null,
) {
    fun humanDifficultyDisplayable(): String {
        val difficulty = humanDifficulty ?: return "?"

        if (solvedViaHumanDifficulty == false) {
            return "$difficulty!"
        }

        if (solvedViaHumanDifficultyIncludingNishio == true) {
            return "$difficulty nishio"
        }

        return difficulty.toString()
    }
}
