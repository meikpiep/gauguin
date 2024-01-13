package org.piepmeyer.gauguin.statistics

import kotlinx.serialization.Serializable

@Serializable
data class Statistics(
    val overall: OverallStatistics = OverallStatistics(),
)

@Serializable
data class OverallStatistics(
    val solvedDifficulty: MutableList<Double> = mutableListOf(),
    val solvedDuration: MutableList<Int> = mutableListOf(),
    var streakSequence: MutableList<Int> = mutableListOf(),
    var gamesStarted: Int = 0,
    var gamesSolved: Int = 0,
    var gamesSolvedWithHints: Int = 0,
    var longestStreak: Int = 0,
    var solvedDifficultyMinimum: Double = Double.MAX_VALUE,
    var solvedDifficultyMaximum: Double = 0.0,
    var solvedDifficultySum: Double = 0.0,
    var solvedDurationMinimum: Int = Int.MAX_VALUE,
    var solvedDurationMaximum: Int = 0,
    var solvedDurationSum: Int = 0,
)
