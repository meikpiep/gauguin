package org.piepmeyer.gauguin.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.piepmeyer.gauguin.creation.MergingCageGridCalculator
import org.piepmeyer.gauguin.creation.RandomPossibleDigitsShuffler
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.difficulty.human.HumanSolver
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import kotlin.time.measureTime

@RunWith(AndroidJUnit4::class)
class HumanSolverBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun log() {
        val randomizer = SeedRandomizerMock(1)

        val calculator =
            MergingCageGridCalculator(
                GameVariant(
                    GridSize(7, 7),
                    GameOptionsVariant.createClassic(),
                ),
                randomizer,
                RandomPossibleDigitsShuffler(randomizer.random),
            )

        val originalGrid =
            runBlocking {
                calculator.calculate()
            }

        benchmarkRule.measureRepeated {
            val grid = originalGrid.copyWithEmptyUserValues()
            grid.cells.forEach { it.possibles = grid.variant.possibleDigits }

            println("org:$originalGrid")
            println("new: $grid")

            val time =
                measureTime {
                    val solver = HumanSolver(grid, true)

                    assert(solver.solveAndCalculateDifficulty().difficulty > 0)
                }

            println("solved: $grid")

            println("time: $time")
        }
    }
}
