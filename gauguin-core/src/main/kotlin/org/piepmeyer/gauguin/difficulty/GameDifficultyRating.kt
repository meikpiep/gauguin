package org.piepmeyer.gauguin.difficulty

import kotlinx.serialization.Serializable

@Serializable
data class GameDifficultyRating(
    val variant: GameDifficultyVariant,
    val thresholdEasy: Double,
    val thresholdMedium: Double,
    val thresholdHard: Double,
    val thresholdExtreme: Double,
) {

    fun threshold (difficulty: GameDifficulty): Double {
        return when (difficulty) {
            GameDifficulty.EASY -> thresholdEasy
            GameDifficulty.MEDIUM -> thresholdMedium
            GameDifficulty.HARD -> thresholdHard
            GameDifficulty.EXTREME -> thresholdExtreme
            else -> throw IllegalArgumentException("Threshold of difficulty $difficulty is not implemented.")
        }
    }

    fun getDifficulty(difficultyValue: Double): GameDifficulty {
        if (difficultyValue >= thresholdExtreme) {
            return GameDifficulty.EXTREME
        }
        if (difficultyValue >= thresholdHard) {
            return GameDifficulty.HARD
        }
        if (difficultyValue >= thresholdMedium) {
            return GameDifficulty.MEDIUM
        }
        return if (difficultyValue >= thresholdEasy) {
            GameDifficulty.EASY
        } else {
            GameDifficulty.VERY_EASY
        }
    }
}
