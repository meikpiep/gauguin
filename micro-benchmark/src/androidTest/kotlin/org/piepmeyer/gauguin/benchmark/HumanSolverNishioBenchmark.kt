package org.piepmeyer.gauguin.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCacheImpl
import org.piepmeyer.gauguin.difficulty.human.strategy.nishio.AdvancedNishioWithPairs
import org.piepmeyer.gauguin.game.save.GridLoaderFromResource
import java.io.File

@RunWith(AndroidJUnit4::class)
class HumanSolverNishioBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun log() {
        val gridResource = this::class.java.getResource("game_7x7_nishio.yml")

        val tempFile = File.createTempFile("bla", "bla")

        val originalGrid =
            GridLoaderFromResource(
                gridResource,
                tempFile,
            ).loadGrid()

        val grid = originalGrid.copyWithEmptyUserValues()

        grid.cells.forEach { it.possibles = grid.variant.possibleDigits }

        val cache = HumanSolverCacheImpl.createValidatedCache(grid)

        benchmarkRule.measureRepeated {
            AdvancedNishioWithPairs().fillCells(grid, cache)
        }
    }
}
