package org.piepmeyer.gauguin.creation

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.RandomSingleton
import org.piepmeyer.gauguin.Randomizer
import org.piepmeyer.gauguin.creation.cage.GridCageCreator
import org.piepmeyer.gauguin.difficulty.GameDifficultyRater
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.GameVariant

private val logger = KotlinLogging.logger {}

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

            logger.debug { "Randomizing grid..." }
            randomiseGrid(newGrid)
            logger.debug { "Creating cages..." }
            createCages(newGrid)
        } while (!isWantedDifficulty(newGrid))

        logger.debug { "Created randomized grid." }
        return newGrid
    }

    private fun isWantedDifficulty(grid: Grid): Boolean {
        if (variant.options.difficultiesSetting == DifficultySetting.all()) {
            return true
        }
        return if (!rater.isSupported(grid.variant)) {
            true
        } else {
            rater.difficulty(variantRating, grid) in variant.options.difficultiesSetting
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
