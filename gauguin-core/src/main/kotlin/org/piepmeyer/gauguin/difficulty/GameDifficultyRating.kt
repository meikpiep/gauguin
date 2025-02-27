package org.piepmeyer.gauguin.difficulty

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.options.DifficultySetting

@Serializable
data class GameDifficultyRating(
    val variant: GameDifficultyVariant,
    val thresholdEasy: Double,
    val thresholdMedium: Double,
    val thresholdHard: Double,
    val thresholdExtreme: Double,
) {
    fun threshold(difficulty: DifficultySetting): Double =
        when (difficulty) {
            DifficultySetting.EASY -> thresholdEasy
            DifficultySetting.MEDIUM -> thresholdMedium
            DifficultySetting.HARD -> thresholdHard
            DifficultySetting.EXTREME -> thresholdExtreme
            else -> throw IllegalArgumentException("Threshold of difficulty $difficulty is not implemented.")
        }

    fun getDifficulty(difficultyValue: Double): DifficultySetting {
        if (difficultyValue >= thresholdExtreme) {
            return DifficultySetting.EXTREME
        }
        if (difficultyValue >= thresholdHard) {
            return DifficultySetting.HARD
        }
        if (difficultyValue >= thresholdMedium) {
            return DifficultySetting.MEDIUM
        }
        return if (difficultyValue >= thresholdEasy) {
            DifficultySetting.EASY
        } else {
            DifficultySetting.VERY_EASY
        }
    }
}
