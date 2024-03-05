package org.piepmeyer.gauguin.difficulty

import kotlinx.serialization.Serializable

@Serializable
data class GameVariantPossibleItem(
    val variant: GameDifficultyVariant,
    val calculatedDifficulties: Int,
)
