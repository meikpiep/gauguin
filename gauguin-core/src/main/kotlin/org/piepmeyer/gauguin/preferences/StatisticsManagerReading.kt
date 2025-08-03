package org.piepmeyer.gauguin.preferences

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.statistics.Statistics
import kotlin.time.Duration

interface StatisticsManagerReading {
    fun currentStreak(): Int

    fun longestStreak(): Int

    fun totalStarted(): Int

    fun totalSolved(): Int

    fun totalHinted(): Int

    fun clearStatistics()

    fun getBestTime(grid: Grid): Duration

    fun typeOfSolution(grid: Grid): TypeOfSolution

    fun statistics(): Statistics
}
