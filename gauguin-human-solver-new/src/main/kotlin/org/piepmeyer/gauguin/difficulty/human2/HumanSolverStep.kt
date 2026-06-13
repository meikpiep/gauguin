package org.piepmeyer.gauguin.difficulty.human2

data class HumanSolverStep(
    val success: Boolean,
    val difficulty: Int,
    val usedNishio: Boolean = false,
)
