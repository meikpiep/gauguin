package org.piepmeyer.gauguin.preferences

import android.content.SharedPreferences
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.piepmeyer.gauguin.difficulty.GridDifficultyCalculator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.statistics.Statistics
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration

private val logger = KotlinLogging.logger {}

class StatisticsManagerImpl(
    directory: File,
    sharedPreferences: SharedPreferences,
) : StatisticsManager {
    private val numberOfItemsOfStore = 50
    private val statisticsFile = File(directory, "statistics.yaml")
    private val legacyManager = LegacyStatisticsManager(sharedPreferences)
    private var statistics: Statistics = loadStatistics()

    override fun puzzleStartedToBePlayed() {
        statistics.overall.gamesStarted++

        saveStatistics()
    }

    override fun puzzleSolved(grid: Grid) {
        statistics.overall.gamesSolved++

        if (grid.isCheated()) {
            statistics.overall.gamesSolvedWithHints++
        }

        val difficulty = GridDifficultyCalculator(grid).calculate()
        val duration = grid.playTime.inWholeSeconds.toInt()

        statistics.overall.solvedDifficulty.add(difficulty)
        statistics.overall.solvedDuration.add(duration)

        if (statistics.overall.solvedDifficulty.size > numberOfItemsOfStore) {
            statistics.overall.solvedDifficulty.removeAt(0)
        }
        if (statistics.overall.solvedDuration.size > numberOfItemsOfStore) {
            statistics.overall.solvedDuration.removeAt(0)
        }

        statistics.overall.solvedDifficultySum += difficulty
        statistics.overall.solvedDurationSum += duration

        statistics.overall.solvedDifficultyMinimum = min(difficulty, statistics.overall.solvedDifficultyMinimum)
        statistics.overall.solvedDurationMinimum = min(duration, statistics.overall.solvedDurationMinimum)

        statistics.overall.solvedDifficultyMaximum = max(difficulty, statistics.overall.solvedDifficultyMaximum)
        statistics.overall.solvedDurationMaximum = max(duration, statistics.overall.solvedDurationMaximum)

        saveStatistics()
    }

    override fun storeStatisticsAfterFinishedGame(grid: Grid) {
        legacyManager.storeStatisticsAfterFinishedGame(grid)
    }

    override fun getBestTime(grid: Grid): Duration {
        return legacyManager.getBestTime(grid)
    }

    override fun storeStreak(isSolved: Boolean) {
        if (isSolved) {
            val newStreak = currentStreak() + 1

            statistics.overall.streakSequence += newStreak
            if (newStreak > longestStreak()) {
                statistics.overall.longestStreak = newStreak
            }
        } else {
            statistics.overall.streakSequence += 0
        }

        if (statistics.overall.streakSequence.size > numberOfItemsOfStore) {
            statistics.overall.streakSequence.removeAt(0)
        }

        saveStatistics()
    }

    private fun loadStatistics(): Statistics {
        if (!statisticsFile.exists()) {
            return Statistics()
        }

        return try {
            val fileData = statisticsFile.readText(StandardCharsets.UTF_8)

            Json.decodeFromString<Statistics>(fileData)
        } catch (e: IOException) {
            logger.error(e) { "Error loading statistics: " + e.message }
            Statistics()
        }
    }

    private fun saveStatistics() {
        try {
            val result = Json.encodeToString(statistics)

            statisticsFile.writeText(result)
        } catch (e: IOException) {
            logger.error(e) { "Error saving statistics: " + e.message }
            return
        }
    }

    override fun currentStreak() = statistics.overall.streakSequence.lastOrNull() ?: 0

    override fun longestStreak() = statistics.overall.longestStreak

    override fun totalStarted() = statistics.overall.gamesStarted

    override fun totalSolved() = statistics.overall.gamesSolved

    override fun totalHinted() = statistics.overall.gamesSolvedWithHints

    override fun clearStatistics() {
        statistics = Statistics()
        saveStatistics()

        legacyManager.clearStatistics()
    }

    override fun statistics(): Statistics {
        return statistics
    }

    override fun typeOfSolution(grid: Grid): TypeOfSolution {
        if (totalSolved() == 1) {
            return TypeOfSolution.FirstGame
        }

        if (grid.solvedFirstTimeOfKind) {
            return TypeOfSolution.FirstGameOfKind
        }

        if (grid.solvedBestTimeOfKind) {
            return TypeOfSolution.BestTimeOfKind
        }

        return TypeOfSolution.Regular
    }
}
