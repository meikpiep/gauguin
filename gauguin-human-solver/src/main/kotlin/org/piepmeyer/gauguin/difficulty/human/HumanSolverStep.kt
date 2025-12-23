package org.piepmeyer.gauguin.difficulty.human

data class HumanSolverStep(
    val success: Boolean,
    val difficulty: Int,
    val usedNishio: Boolean = false,
)
