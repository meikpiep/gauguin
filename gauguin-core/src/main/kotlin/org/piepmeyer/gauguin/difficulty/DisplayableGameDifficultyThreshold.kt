package org.piepmeyer.gauguin.difficulty

import java.math.BigDecimal

class DisplayableGameDifficultyThreshold(
    private val rating: GameDifficultyRating
) {

    fun thresholdText(difficulty: GameDifficulty): BigDecimal {
        val threshold = rating.threshold(difficulty)

        return DisplayableGameDifficulty(rating).displayableDifficultyValue(threshold)
    }
}
