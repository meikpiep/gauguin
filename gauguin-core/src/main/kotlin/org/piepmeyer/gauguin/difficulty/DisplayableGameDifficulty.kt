package org.piepmeyer.gauguin.difficulty

import org.piepmeyer.gauguin.grid.Grid
import java.math.BigDecimal
import java.math.RoundingMode

class DisplayableGameDifficulty(
    private val rating: GameDifficultyRating?,
) {
    fun displayableDifficultyValue(threshold: Double): BigDecimal {
        val nonIntegerValuesThreshold = rating?.thresholdExtreme ?: threshold

        return if (nonIntegerValuesThreshold < 20) {
            threshold.toBigDecimal().setScale(1, RoundingMode.HALF_UP)
        } else {
            threshold.toBigDecimal().setScale(0, RoundingMode.HALF_UP)
        }
    }

    fun displayableDifficulty(grid: Grid): BigDecimal {
        val difficultyValue = GridDifficultyCalculator(grid).calculate()

        return displayableDifficultyValue(difficultyValue)
    }
}
