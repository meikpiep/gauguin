package org.piepmeyer.gauguin.creation

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.FunSpec
import org.piepmeyer.gauguin.difficulty.GridDifficultyCalculator
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant

private val logger = KotlinLogging.logger {}

class MergingCageGridCalculatorTest : FunSpec({

    test("6x6 gets calculated") {

        val randomizer = SeedRandomizerMock(1)

        val calculator =
            MergingCageGridCalculator(
                variant = GameVariant(GridSize(6, 6), GameOptionsVariant.createClassic()),
                randomizer = randomizer,
                shuffler = RandomPossibleDigitsShuffler(randomizer.random),
            )

        val grid = calculator.calculate()

        logger.info { grid }

        logger.info { GridDifficultyCalculator(grid).calculate() }
    }
})
