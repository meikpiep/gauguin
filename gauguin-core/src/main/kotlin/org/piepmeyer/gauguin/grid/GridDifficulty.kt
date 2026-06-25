package org.piepmeyer.gauguin.grid

data class GridDifficulty(
    val classicalRating: Double? = null,
    val humanDifficulty: Int? = null,
    val humanDifficulty2: Int? = null,
    val solvedViaHumanDifficulty: Boolean? = null,
    val solvedViaHumanDifficultyIncludingNishio: Boolean? = null,
) {
    fun humanDifficultyDisplayable(): String {
        val difficulty = humanDifficulty ?: return "?"

        if (solvedViaHumanDifficulty == false) {
            return "$difficulty - $humanDifficulty2!"
        }

        if (solvedViaHumanDifficultyIncludingNishio == true) {
            return "$difficulty - $humanDifficulty2 nishio"
        }

        return "$difficulty - $humanDifficulty2"
    }
}
