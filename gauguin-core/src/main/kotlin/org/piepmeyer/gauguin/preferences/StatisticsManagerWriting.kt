package org.piepmeyer.gauguin.preferences

import org.piepmeyer.gauguin.grid.Grid

interface StatisticsManagerWriting {
    fun puzzleStartedToBePlayed()

    fun puzzleSolved(grid: Grid)

    fun storeStatisticsAfterFinishedGame(grid: Grid)

    fun storeStreak(isSolved: Boolean)
}
