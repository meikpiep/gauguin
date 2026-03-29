package org.piepmeyer.gauguin.history

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.grid.GridSize
import kotlin.time.Duration

class History(
    override val events: List<HistoryEvent>,
) : HistoryView(events) {
    fun view(size: GridSize): HistoryView {
        val filteredEvents = events.filter { it.gridInfo.size == size }

        return HistoryView(filteredEvents)
    }

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
    val size: GridSize,
    val classicDifficulty: Double,
    val duration: Duration,
)
