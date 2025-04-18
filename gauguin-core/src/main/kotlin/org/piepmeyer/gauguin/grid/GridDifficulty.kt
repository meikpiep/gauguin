package org.piepmeyer.gauguin.grid

data class GridDifficulty(
    val classicalRating: Double? = null,
    val humanDifficulty: Int? = null,
    val solvedViaHumanDifficulty: Boolean? = null,
)
