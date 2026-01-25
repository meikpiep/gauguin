package org.piepmeyer.gauguin.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.piepmeyer.gauguin.difficulty.human.HumanSolver
import org.piepmeyer.gauguin.game.save.GridLoaderFromResource
import java.io.File

@RunWith(AndroidJUnit4::class)
class HumanSolverUnsolved7x7Benchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun test() {
        val gridResource = this::class.java.getResource("game_unsolved_7x7.yml")

        val tempFile = File.createTempFile("bla", "bla")

        val originalGrid =
            GridLoaderFromResource(
                gridResource,
                tempFile,
            ).loadGrid()

        benchmarkRule.measureRepeated {
            val grid = originalGrid.copyWithEmptyUserValues()

            // println("org:$originalGrid")
            // println("new: $grid")

            val solver = HumanSolver(grid, true)
            solver.prepareGrid()

            assert(solver.solveAndCalculateDifficulty().difficulty > 0)

            // println("solved: $grid")
        }
    }
}
