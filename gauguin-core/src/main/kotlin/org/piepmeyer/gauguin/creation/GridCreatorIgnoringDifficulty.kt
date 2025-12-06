package org.piepmeyer.gauguin.creation

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.RandomSingleton
import org.piepmeyer.gauguin.Randomizer
import org.piepmeyer.gauguin.creation.cage.GridCageCreator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.GameVariant

private val logger = KotlinLogging.logger {}

class GridCreatorIgnoringDifficulty(
    private val variant: GameVariant,
    private val randomizer: Randomizer = RandomSingleton.instance,
    private val shuffler: PossibleDigitsShuffler = RandomPossibleDigitsShuffler(),
) {
    init {
        randomizer.discard()
    }

    fun createRandomizedGridWithCages(): Grid {
        val grid = Grid(variant)

        logger.debug { "Randomizing grid..." }
        randomiseGrid(grid)
        logger.debug { "Creating cages..." }
        createCages(grid)

        return grid
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
