package org.piepmeyer.gauguin.creation

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.piepmeyer.gauguin.difficulty.AlternativeGridDifficultyCalculator
import org.piepmeyer.gauguin.difficulty.human.HumanDifficultyCalculatorImpl
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.options.SingleCageUsage

private val logger = KotlinLogging.logger {}

class TestMergingCageGridCalculatorDistribution :
    FunSpec({
        xtest("calculateValues 7x7") {
            testHundredGrids(7)
        }

        xtest("calculateValues 9x9") {
            testHundredGrids(9)
        }
    }) {
    companion object {
        private fun testHundredGrids(size: Int) {
            val grids =
                runBlocking(Dispatchers.Default) {
                    calculateDifficulties(size).awaitAll()
                }

            val difficultiesAndSingles =
                grids
                    .map {
                        it.difficulty.humanDifficulty!!
                    }

            val sortedDifficulties = difficultiesAndSingles.sorted()

            logger.info {
                "Difficulties: minimum ${sortedDifficulties.min()}, " +
                    "average ${sortedDifficulties.average()}, " +
                    "maximum ${sortedDifficulties.max()}"
            }

            logger.info {
                "Difficulties: 10th ${sortedDifficulties[9]}, " +
                    "20th ${sortedDifficulties[19]}"
            }

            val gridsWithDifficulties = grids.associateBy { it.difficulty.humanDifficulty!! }.toSortedMap()

            gridsWithDifficulties.forEach {
                logger.info { "difficulty ${it.key} (${AlternativeGridDifficultyCalculator(it.value).calculate()})" }
            }
        }

        private suspend fun calculateDifficulties(size: Int): List<Deferred<Grid>> =
            coroutineScope {
                val deferreds = mutableListOf<Deferred<Grid>>()

                val variant =
                    GameVariant(
                        GridSize(size, size),
                        GameOptionsVariant(
                            true,
                            GridCageOperation.OPERATIONS_ALL,
                            DigitSetting.FIRST_DIGIT_ONE,
                            setOf(DifficultySetting.EXTREME),
                            SingleCageUsage.FIXED_NUMBER,
                            NumeralSystem.Decimal,
                        ),
                    )

                for (i in 0..99) {
                    val randomizer = SeedRandomizerMock(i)
                    val creator = MergingCageGridCalculator(variant, randomizer, RandomPossibleDigitsShuffler(randomizer.random))

                    deferreds +=
                        async(CoroutineName(variant.toString())) {
                            calculateOneDifficulty(
                                creator,
                            )
                        }
                }

                return@coroutineScope deferreds
            }

        private suspend fun calculateOneDifficulty(creator: MergingCageGridCalculator): Grid {
            val grid = creator.calculate()

            HumanDifficultyCalculatorImpl(grid).ensureDifficultyCalculated()

            logger.info { "finished ${grid.variant}" }

            return grid
        }
    }
}
