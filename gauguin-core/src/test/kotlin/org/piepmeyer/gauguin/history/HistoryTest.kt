package org.piepmeyer.gauguin.history

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.grid.GridSize
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class HistoryTest :
    FunSpec({

        test("empty history") {
            val history = History(emptyList())

            history.solvedGrids() shouldBe 0
            history.solvedDifficulty() shouldBe 0.0
            history.solvedDuration() shouldBe 0.minutes
            history.playedGrids() shouldBe 0
            history.playedDifficulty() shouldBe 0.0
            history.playedDuration() shouldBe 0.minutes
            history.currentStreak() shouldBe 0
            history.longestStreak() shouldBe 0
        }

        test("single solved grid") {
            val history =
                History(
                    listOf(
                        HistoryEvent.GridSolved(historyInfo(10.0, 5.minutes)),
                    ),
                )

            history.solvedGrids() shouldBe 1
            history.solvedDifficulty() shouldBe 10.0
            history.solvedDuration() shouldBe 5.minutes
            history.playedGrids() shouldBe 1
            history.playedDifficulty() shouldBe 10.0
            history.playedDuration() shouldBe 5.minutes
            history.currentStreak() shouldBe 1
            history.longestStreak() shouldBe 1
        }

        test("two solved grids") {
            val history =
                History(
                    listOf(
                        HistoryEvent.GridSolved(historyInfo(10.0, 5.minutes)),
                        HistoryEvent.GridSolved(historyInfo(15.5, 7.minutes)),
                    ),
                )

            history.solvedGrids() shouldBe 2
            history.solvedDifficulty() shouldBe 25.5
            history.solvedDuration() shouldBe 12.minutes
            history.playedGrids() shouldBe 2
            history.playedDifficulty() shouldBe 25.5
            history.playedDuration() shouldBe 12.minutes
            history.currentStreak() shouldBe 2
            history.longestStreak() shouldBe 2
        }

        test("single unsolved grid") {
            val history =
                History(
                    listOf(
                        HistoryEvent.GridUnsolved(historyInfo(10.0, 5.minutes)),
                    ),
                )

            history.solvedGrids() shouldBe 0
            history.solvedDifficulty() shouldBe 0
            history.solvedDuration() shouldBe 0.minutes
            history.playedGrids() shouldBe 1
            history.playedDifficulty() shouldBe 10.0
            history.playedDuration() shouldBe 5.minutes
            history.currentStreak() shouldBe 0
            history.longestStreak() shouldBe 0
        }

        test("streaks solved solved unsolved solved") {
            val history =
                History(
                    listOf(
                        HistoryEvent.GridSolved(historyInfo()),
                        HistoryEvent.GridSolved(historyInfo()),
                        HistoryEvent.GridUnsolved(historyInfo()),
                        HistoryEvent.GridSolved(historyInfo()),
                    ),
                )

            history.currentStreak() shouldBe 1
            history.longestStreak() shouldBe 2
            history.streaks() shouldContainExactly listOf(2, 0, 1)
        }

        test("filter to grid size returns single grid") {
            val history =
                History(
                    listOf(
                        HistoryEvent.GridSolved(historyInfo(10.0, 5.minutes, GridSize(3, 3))),
                        HistoryEvent.GridSolved(historyInfo(11.0, 6.minutes, GridSize(4, 4))),
                        HistoryEvent.GridSolved(historyInfo(12.0, 7.minutes, GridSize(5, 5))),
                    ),
                ).view(size = GridSize(3, 3))

            history.solvedGrids() shouldBe 1
            history.solvedDifficulty() shouldBe 10.0
            history.solvedDuration() shouldBe 5.minutes
            history.playedGrids() shouldBe 1
            history.playedDifficulty() shouldBe 10.0
            history.playedDuration() shouldBe 5.minutes
        }
    })

private fun historyInfo(
    difficulty: Double = 0.0,
    duration: Duration = 0.minutes,
    size: GridSize = GridSize(3, 3),
): GridHistoryInfo = GridHistoryInfo(size, difficulty, duration)
