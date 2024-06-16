package org.piepmeyer.gauguin.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import org.piepmeyer.gauguin.grid.Grid
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

class LegacyStatisticsManager(
    private val stats: SharedPreferences,
) {
    fun storeStatisticsAfterFinishedGame(grid: Grid) {
        val key = getBestTimeKey(grid)
        val solveTime = grid.playTime

        val bestTime = stats.getLong(key, 0).milliseconds

        if (isNewBestTime(solveTime, bestTime)) {
            stats.edit { putLong(key, solveTime.inWholeMilliseconds) }

            if (bestTime == ZERO) {
                grid.solvedFirstTimeOfKind = true
            } else {
                grid.solvedBestTimeOfKind = true
            }
        }
    }

    fun isNewBestTime(
        solveTime: Duration,
        bestTime: Duration,
    ) = bestTime == ZERO || bestTime.inWholeSeconds > solveTime.inWholeSeconds

    fun getBestTime(grid: Grid): Duration {
        val key = getBestTimeKey(grid)

        return stats.getLong(key, 0).milliseconds
    }

    private fun getBestTimeKey(grid: Grid) = "solvedtime${grid.gridSize}"

    fun clearStatistics() {
        stats.edit { clear() }
    }
}
