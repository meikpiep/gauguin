package org.piepmeyer.gauguin.creation

import org.piepmeyer.gauguin.RandomSingleton
import org.piepmeyer.gauguin.Randomizer
import org.piepmeyer.gauguin.creation.cage.GridCageCreator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.GameVariant

class GridCreator(
    private val variant: GameVariant,
    private val randomizer: Randomizer = RandomSingleton.instance,
    private val shuffler: PossibleDigitsShuffler = RandomPossibleDigitsShuffler()
) {
    fun createRandomizedGridWithCages(): Grid {
        randomizer.discard()
        var newGrid: Grid
        do {
            newGrid = Grid(variant)
            randomiseGrid(newGrid)
            createCages(newGrid)
        } while (!isWantedDifficulty(newGrid))

        return newGrid
    }

    private fun isWantedDifficulty(grid: Grid): Boolean {
        if (variant.options.difficultySetting == DifficultySetting.ANY) {
            return true
        }
        val calculator = GridDifficultyCalculator(grid)
        return if (!calculator.isGridVariantSupported) {
            true
        } else {
            calculator.difficulty == variant.options.difficultySetting.gameDifficulty
        }
    }

    private fun createCages(grid: Grid) {
        val creator = GridCageCreator(randomizer, grid)
        creator.createCages()
    }

    private fun randomiseGrid(grid: Grid) {
        val randomizer = GridRandomizer(shuffler, grid)
        randomizer.createGrid()
    }
}
