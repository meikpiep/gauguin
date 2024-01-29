package org.piepmeyer.gauguin.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import org.piepmeyer.gauguin.grid.Grid
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

class StatisticsManagerImpl(
    private val stats: SharedPreferences,
) : StatisticsManager {
    override fun puzzleStartedToBePlayed() {
        stats.edit {
            putInt("totalStarted", totalStarted() + 1)
        }
    }

    override fun puzzleSolved(grid: Grid) {
        stats.edit {
            putInt("totalSolved", totalSolved() + 1)

            if (grid.isCheated()) {
                putInt("totalHinted", totalHinted() + 1)
            }
        }
    }

    override fun storeStatisticsAfterNewGame(grid: Grid) {
        val gamestat = stats.getInt("playedgames" + grid.gridSize, 0)

        stats.edit {
            putInt("playedgames" + grid.gridSize, gamestat + 1)
        }
    }

    override fun storeStatisticsAfterFinishedGame(grid: Grid) {
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

    override fun getBestTime(grid: Grid): Duration {
        val key = getBestTimeKey(grid)

        return stats.getLong(key, 0).milliseconds
    }

    private fun getBestTimeKey(grid: Grid) = "solvedtime${grid.gridSize}"

    override fun storeStreak(isSolved: Boolean) {
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

    override fun currentStreak() = stats.getInt("solvedstreak", 0)

    override fun longestStreak() = stats.getInt("longeststreak", 0)

    override fun totalStarted() = stats.getInt("totalStarted", 0)

    override fun totalSolved() = stats.getInt("totalSolved", 0)

    override fun totalHinted() = stats.getInt("totalHinted", 0)

    override fun clearStatistics() {
        stats.edit { clear() }
    }

    override fun typeOfSolution(grid: Grid): TypeOfSolution {
        if (totalSolved() == 1) {
            return TypeOfSolution.FirstGame
        }

        if (grid.solvedFirstTimeOfKind) {
            return TypeOfSolution.FirstGameOfKind
        }

        if (grid.solvedBestTimeOfKind) {
            println("-> " + getBestTime(grid))
            println(grid.playTime)
            return TypeOfSolution.BestTimeOfKind
        }

        return TypeOfSolution.Regular
    }
}
