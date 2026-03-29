package org.piepmeyer.gauguin.history

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

open class HistoryView(
    open val events: List<HistoryEvent>,
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
}
