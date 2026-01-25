package org.piepmeyer.gauguin.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.piepmeyer.gauguin.creation.RandomCageGridCalculator
import org.piepmeyer.gauguin.creation.RandomPossibleDigitsShuffler
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.difficulty.human.HumanSolver
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant

@RunWith(AndroidJUnit4::class)
class HumanSolver5x5SeedBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun log() {
        val randomizer = SeedRandomizerMock(1)

        val calculator =
            RandomCageGridCalculator(
                GameVariant(
                    GridSize(5, 5),
                    GameOptionsVariant.createClassic(),
                ),
                randomizer,
                RandomPossibleDigitsShuffler(randomizer.random),
            )

        val orgGrid =
            runBlocking {
                calculator.calculate()
            }

        benchmarkRule.measureRepeated {
            val grid = orgGrid.copyWithEmptyUserValues()

            grid.cells.forEach { it.possibles = grid.variant.possibleDigits }

            val solver = HumanSolver(grid)
            solver.prepareGrid()

            val solverResult = solver.solveAndCalculateDifficulty()

            println(grid.toString())

            if (!grid.isSolved()) {
                if (grid.numberOfMistakes() != 0) {
                    throw IllegalStateException("Found a grid with wrong values.")
                }
            }

            assert(solverResult.difficulty > 0)

            // println("solved: $grid")
        }
    }
}
