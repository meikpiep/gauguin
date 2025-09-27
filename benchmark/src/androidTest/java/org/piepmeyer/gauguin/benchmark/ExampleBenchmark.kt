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

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class ExampleBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun log() {
        benchmarkRule.measureRepeated {
            pauseMeasurement()
            val randomizer = SeedRandomizerMock(1)

            val calculator =
                MergingCageGridCalculator(
                    GameVariant(
                        GridSize(9, 9),
                        GameOptionsVariant.createClassic(),
                    ),
                    randomizer,
                    RandomPossibleDigitsShuffler(randomizer.random),
                )

            val grid = runBlocking { calculator.calculate() }
            grid.cells.forEach { it.possibles = grid.variant.possibleDigits }

            resumeMeasurement()

            val solver = HumanSolver(grid, false)

            assert(solver.solveAndCalculateDifficulty().difficulty > 0)
        }
    }
}
