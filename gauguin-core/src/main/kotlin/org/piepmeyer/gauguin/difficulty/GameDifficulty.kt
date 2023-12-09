package org.piepmeyer.gauguin.difficulty

enum class GameDifficulty(private val minimumPercentage: Double) {
    VERY_EASY(0.0),
    EASY(5.0),
    MEDIUM(33.33),
    HARD(66.67),
    EXTREME(95.0),
}
