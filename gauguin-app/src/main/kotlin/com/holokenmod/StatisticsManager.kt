package com.holokenmod

import android.content.Context
import android.content.SharedPreferences
import com.holokenmod.grid.Grid
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

class StatisticsManager(context: Context, private val grid: Grid) {
    private val stats: SharedPreferences

    init {
        stats = context.getSharedPreferences("stats", Context.MODE_PRIVATE)
    }

    fun storeStatisticsAfterNewGame() {
        val gamestat = stats.getInt("playedgames" + grid.gridSize, 0)
        val editor = stats.edit()
        editor.putInt("playedgames" + grid.gridSize, gamestat + 1)
        editor.apply()
    }

    fun storeStatisticsAfterFinishedGame(): String? {
        val gridsize = grid.gridSize

        // assess hint penalty - gridsize^2/2 seconds for each cell
        val penalty = grid.countCheated().toLong() * 500 * gridsize.surfaceArea
        grid.playTime = grid.playTime + penalty.milliseconds
        val solvetime = grid.playTime
        var solveStr = Utils.displayableGameDuration(solvetime)
        val hintedstat = stats.getInt("hintedgames$gridsize", 0)
        val solvedstat = stats.getInt("solvedgames$gridsize", 0)
        val timestat = stats.getLong("solvedtime$gridsize", 0).milliseconds
        val totaltimestat = stats.getLong("totaltime$gridsize", 0).milliseconds
        val editor = stats.edit()
        if (penalty != 0L) {
            editor.putInt("hintedgames$gridsize", hintedstat + 1)
            solveStr += "^"
        } else {
            editor.putInt("solvedgames$gridsize", solvedstat + 1)
        }
        editor.putLong("totaltime$gridsize", (totaltimestat + solvetime).inWholeMilliseconds)
        val recordTime = if (timestat == ZERO || timestat > solvetime) {
            editor.putLong("solvedtime$gridsize", solvetime.inWholeMilliseconds)
            solveStr
        } else {
            null
        }
        editor.apply()
        return recordTime
    }

    fun storeStreak(isSolved: Boolean) {
        val solvedStreak = stats.getInt("solvedstreak", 0)
        val longestStreak = stats.getInt("longeststreak", 0)
        val editor = stats.edit()
        if (isSolved) {
            editor.putInt("solvedstreak", solvedStreak + 1)
            if (solvedStreak == longestStreak) {
                editor.putInt("longeststreak", solvedStreak + 1)
            }
        } else {
            editor.putInt("solvedstreak", 0)
        }
        editor.apply()
    }
}