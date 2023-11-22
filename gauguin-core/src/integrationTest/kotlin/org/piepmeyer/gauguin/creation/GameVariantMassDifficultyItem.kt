package org.piepmeyer.gauguin.creation

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.options.GameVariant

@Serializable
data class GameVariantMassDifficultyItem(
    val variant: GameVariant,
    val calculatedDifficulties: List<Double>
)
