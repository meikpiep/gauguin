package org.piepmeyer.gauguin.creation

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.difficulty.GameDifficultyVariant

@Serializable
data class GameVariantMassDifficultyItem(
    val variant: GameDifficultyVariant,
    val calculatedDifficulties: List<Double>,
)
