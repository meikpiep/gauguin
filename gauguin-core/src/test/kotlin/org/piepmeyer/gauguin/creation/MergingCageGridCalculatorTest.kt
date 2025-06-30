package org.piepmeyer.gauguin.creation

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThan
import org.piepmeyer.gauguin.difficulty.ensureDifficultyCalculated
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.GridCageOperation

private val logger = KotlinLogging.logger {}

class MergingCageGridCalculatorTest :
    FunSpec({

        test("6x6 gets calculated") {

            val randomizer = SeedRandomizerMock(1)

            val calculator =
                MergingCageGridCalculator(
                    variant = GameVariant(GridSize(6, 6), GameOptionsVariant.createClassic()),
                    randomizer = randomizer,
                    shuffler = RandomPossibleDigitsShuffler(randomizer.random),
                )

            val grid = calculator.calculate()

            grid.cages.size shouldBeLessThan 20
        }

        test("3x6 gets calculated") {

            val randomizer = SeedRandomizerMock(1)

            val calculator =
                MergingCageGridCalculator(
                    variant = GameVariant(GridSize(3, 6), GameOptionsVariant.createClassic()),
                    randomizer = randomizer,
                    shuffler = RandomPossibleDigitsShuffler(randomizer.random),
                )

            val grid = calculator.calculate()

            grid.cages.size shouldBeLessThan 10
        }

        xtest("9x9 with plus and minus gets calculated") {

            val randomizer = SeedRandomizerMock(0)

            val calculator =
                MergingCageGridCalculator(
                    variant =
                        GameVariant(
                            GridSize(9, 9),
                            GameOptionsVariant.createClassic().copy(cageOperation = GridCageOperation.OPERATIONS_ADD_SUB),
                        ),
                    randomizer = randomizer,
                    shuffler = RandomPossibleDigitsShuffler(randomizer.random),
                )

            val grid = calculator.calculate()

            logger.info { grid }

            logger.info { grid.ensureDifficultyCalculated() }
        }
    })
