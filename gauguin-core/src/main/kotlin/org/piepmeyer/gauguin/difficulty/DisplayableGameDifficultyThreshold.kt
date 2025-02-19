package org.piepmeyer.gauguin.difficulty

import org.piepmeyer.gauguin.options.DifficultySetting
import java.math.BigDecimal

class DisplayableGameDifficultyThreshold(
    private val rating: GameDifficultyRating,
) {
    fun thresholdText(difficulty: DifficultySetting): BigDecimal {
        val threshold = rating.threshold(difficulty)

        return DisplayableGameDifficulty(rating).displayableDifficultyValue(threshold)
    }
}
