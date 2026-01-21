package org.piepmeyer.gauguin.preferences

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.piepmeyer.gauguin.difficulty.ensureDifficultyCalculated
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.statistics.Statistics
import java.io.File
import java.io.IOException
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

class StatisticsManagerImpl(
    directory: File,
    // sharedPreferences: SharedPreferences,
) : StatisticsManagerWriting,
    StatisticsManagerReading {
    private val numberOfItemsOfStore = 50
    private val statisticsFile = File(directory, "statistics.yaml")

    // private val legacyManager = LegacyStatisticsManager(sharedPreferences)
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

        val difficulty = grid.ensureDifficultyCalculated()
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
        // legacyManager.storeStatisticsAfterFinishedGame(grid)
    }

    override fun getBestTime(grid: Grid): Duration = 999.seconds // legacyManager.getBestTime(grid)

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

    override fun endCurrentGame(grid: Grid) {
        if (grid.isActive && !grid.isSolved() && grid.startedToBePlayed) {
            storeStreak(false)
        }
    }

    override fun gridSolvedByEnteringNumber(grid: Grid) {
        if (!grid.isCheated()) {
            puzzleSolved(grid)
            storeStatisticsAfterFinishedGame(grid)
        }

        storeStreak(!grid.isCheated())
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadStatistics(): Statistics {
        return runBlocking {
            async(Dispatchers.IO) {
                if (!statisticsFile.exists()) {
                    return@async Statistics()
                }

                return@async try {
                    statisticsFile.inputStream().use {
                        Json.decodeFromStream<Statistics>(it)
                    }
                } catch (e: IOException) {
                    logger.error(e) { "Error loading statistics: " + e.message }
                    Statistics()
                }
            }.await()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun saveStatistics() {
        try {
            statisticsFile.outputStream().use {
                Json.encodeToStream(statistics, it)
            }
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

        // legacyManager.clearStatistics()
    }

    override fun statistics(): Statistics = statistics

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
