package org.piepmeyer.gauguin

import android.content.Context
import androidx.core.content.edit
import org.piepmeyer.gauguin.grid.Grid
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

class StatisticsManager(
    context: Context
) {
    private val stats = context.getSharedPreferences("stats", Context.MODE_PRIVATE)

    fun storeStatisticsAfterNewGame(grid: Grid) {
        val gamestat = stats.getInt("playedgames" + grid.gridSize, 0)

        stats.edit {
            putInt("playedgames" + grid.gridSize, gamestat + 1)
        }
    }

    fun storeStatisticsAfterFinishedGame(grid: Grid): String? {
        val gridsize = grid.gridSize

        // assess hint penalty - gridsize^2/2 seconds for each cell
        val penalty = grid.countCheated().toLong() * 500 * gridsize.surfaceArea
        grid.playTime = grid.playTime + penalty.milliseconds
        val solvetime = grid.playTime

        val timestat = stats.getLong("solvedtime$gridsize", 0).milliseconds
        val editor = stats.edit()
        val recordTime = if (timestat == ZERO || timestat > solvetime) {
            editor.putLong("solvedtime$gridsize", solvetime.inWholeMilliseconds)
            Utils.displayableGameDuration(solvetime)
        } else {
            null
        }
        editor.apply()
        return recordTime
    }

    fun storeStreak(isSolved: Boolean) {
        val solvedStreak = currentStreak()
        val longestStreak = longestStreak()

        stats.edit {
            if (isSolved) {
                putInt("solvedstreak", solvedStreak + 1)
                if (solvedStreak == longestStreak) {
                    putInt("longeststreak", solvedStreak + 1)
                }
            } else {
                putInt("solvedstreak", 0)
            }
        }
    }

    fun currentStreak() = stats.getInt("solvedstreak", 0)

    fun longestStreak() = stats.getInt("longeststreak", 0)

    fun totalStarted() = stats.getInt("totalStarted", 0)

    fun totalSolved() = stats.getInt("totalStarted", 0)

    fun totalHinted() = stats.getInt("totalHinted", 0)

    fun clearStatistics() {
        stats.edit { clear() }
    }
}