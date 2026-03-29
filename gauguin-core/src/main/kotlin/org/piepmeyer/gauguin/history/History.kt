package org.piepmeyer.gauguin.history

import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Serializable
data class History(
    val events: List<HistoryEvent>,
) {
    fun playedGrids(): Int = events.size

    fun playedDifficulty(): Double = events.sumOf { it.gridInfo.classicDifficulty }

    fun playedDuration(): Duration =
        events
            .map { it.gridInfo.duration }
            .reduceOrNull { acc, duration -> acc + duration } ?: 0.minutes

    fun solvedGrids(): Int = events.count { it is HistoryEvent.GridSolved }

    fun solvedDifficulty(): Double =
        events
            .filterIsInstance<HistoryEvent.GridSolved>()
            .sumOf { it.gridInfo.classicDifficulty }

    fun solvedDuration(): Duration =
        events
            .filterIsInstance<HistoryEvent.GridSolved>()
            .map { it.gridInfo.duration }
            .reduceOrNull { acc, duration -> acc + duration } ?: 0.minutes

    fun currentStreak(): Int = events.size - events.indexOfLast { it is HistoryEvent.GridUnsolved } - 1

    fun longestStreak(): Int = streaks().maxOrNull() ?: 0

    fun streaks(): List<Int> {
        val streaks = mutableListOf<Int>()
        var currentStreak = 0

        events.forEach {
            if (it is HistoryEvent.GridSolved) {
                currentStreak++
            }

            if (it is HistoryEvent.GridUnsolved || events.last() == it) {
                streaks += currentStreak

                if (it is HistoryEvent.GridUnsolved) {
                    streaks += 0
                }

                currentStreak = 0
            }
        }

        return streaks
    }
}

@Serializable
sealed class HistoryEvent(
    open val gridInfo: GridHistoryInfo,
) {
    class GridSolved(
        override val gridInfo: GridHistoryInfo,
    ) : HistoryEvent(gridInfo)

    class GridUnsolved(
        override val gridInfo: GridHistoryInfo,
    ) : HistoryEvent(gridInfo)
}

@Serializable
data class GridHistoryInfo(
    val classicDifficulty: Double,
    val duration: Duration,
)
