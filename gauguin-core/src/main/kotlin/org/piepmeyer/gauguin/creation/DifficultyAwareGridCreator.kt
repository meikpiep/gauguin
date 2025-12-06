package org.piepmeyer.gauguin.creation

import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.RandomSingleton
import org.piepmeyer.gauguin.Randomizer
import org.piepmeyer.gauguin.difficulty.GameDifficultyRatingService
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.GameVariant

private val logger = KotlinLogging.logger {}

class DifficultyAwareGridCreator(
    private val variant: GameVariant,
    private val randomizer: Randomizer = RandomSingleton.instance,
    private val shuffler: PossibleDigitsShuffler = RandomPossibleDigitsShuffler(),
) : KoinComponent {
    private val difficultyService: GameDifficultyRatingService by inject()

    fun createRandomizedGridWithCages(): Grid {
        val coreCreator = GridCreator(variant, randomizer, shuffler)

        var newGrid: Grid
        do {
            newGrid = coreCreator.createRandomizedGridWithCages()
        } while (!isWantedDifficulty(newGrid))

        logger.debug { "Created randomized grid." }
        return newGrid
    }

    private fun isWantedDifficulty(grid: Grid): Boolean {
        if (variant.options.difficultiesSetting == DifficultySetting.all()) {
            return true
        }
        return if (!difficultyService.isSupported(grid.variant)) {
            true
        } else {
            difficultyService.difficultyOfGrid(grid) in variant.options.difficultiesSetting
        }
    }
}
