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
        val solvetime = grid.playTime

        val bestTime = stats.getLong(key, 0).milliseconds

        if (bestTime == ZERO || bestTime > solvetime) {
            stats.edit { putLong(key, solvetime.inWholeMilliseconds) }

            if (bestTime == ZERO) {
                grid.solvedFirstTimeOfKind = true
            } else {
                grid.solvedBestTimeOfKind = true
            }
        }
    }

    fun getBestTime(grid: Grid): Duration {
        val key = getBestTimeKey(grid)

        return stats.getLong(key, 0).milliseconds
    }

    private fun getBestTimeKey(grid: Grid) = "solvedtime${grid.gridSize}"

    fun currentStreak() = stats.getInt("solvedstreak", 0)

    fun longestStreak() = stats.getInt("longeststreak", 0)

    fun totalStarted() = stats.getInt("totalStarted", 0)

    fun totalSolved() = stats.getInt("totalSolved", 0)

    fun totalHinted() = stats.getInt("totalHinted", 0)

    fun clearStatistics() {
        stats.edit { clear() }
    }
}
