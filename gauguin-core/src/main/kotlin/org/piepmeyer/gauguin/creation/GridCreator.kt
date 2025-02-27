package org.piepmeyer.gauguin.creation

import org.piepmeyer.gauguin.RandomSingleton
import org.piepmeyer.gauguin.Randomizer
import org.piepmeyer.gauguin.creation.cage.GridCageCreator
import org.piepmeyer.gauguin.difficulty.GameDifficultyRater
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.GameVariant

class GridCreator(
    private val variant: GameVariant,
    private val randomizer: Randomizer = RandomSingleton.instance,
    private val shuffler: PossibleDigitsShuffler = RandomPossibleDigitsShuffler(),
) {
    private val rater = GameDifficultyRater()
    private val variantRating = rater.byVariant(variant)

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
        return if (!rater.isSupported(grid.variant)) {
            true
        } else {
            rater.difficulty(variantRating, grid) == variant.options.difficultySetting
        }
    }

    private fun createCages(grid: Grid) {
        val creator = GridCageCreator(randomizer, grid)
        creator.createCages()
    }

    private fun randomiseGrid(grid: Grid) {
        val randomizer = GridRandomizer(shuffler, grid)
        randomizer.createGridValues()
    }
}
