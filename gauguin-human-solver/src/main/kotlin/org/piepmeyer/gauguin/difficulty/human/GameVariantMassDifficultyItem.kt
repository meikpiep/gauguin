package org.piepmeyer.gauguin.difficulty.human

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.difficulty.GameDifficultyVariant

@Serializable
data class HumanGameVariantMassDifficultyItem(
    val variant: GameDifficultyVariant,
    val calculatedDifficulties: List<Int>,
)
