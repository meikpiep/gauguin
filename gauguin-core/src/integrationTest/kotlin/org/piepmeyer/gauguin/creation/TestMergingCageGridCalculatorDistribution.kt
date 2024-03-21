package org.piepmeyer.gauguin.creation

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.piepmeyer.gauguin.difficulty.GridDifficultyCalculator
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.options.SingleCageUsage

private val logger = KotlinLogging.logger {}

class TestMergingCageGridCalculatorDistribution : FunSpec({
    xtest("calculateValues 6x6") {
        testHundredGrids(6)
    }

    xtest("calculateValues 9x9") {
        testHundredGrids(9)
    }
}) {
    companion object {
        private fun testHundredGrids(size: Int) {
            val difficultiesAndSingles =
                runBlocking(Dispatchers.Default) {
                    calculateDifficulties(size)
                        .map {
                            val value = it.await()
                            value
                        }
                }

            val sortedDifficulties = difficultiesAndSingles.map { it.first }.sorted()
            val sortedSingles = difficultiesAndSingles.map { it.second }.sorted()

            logger.info {
                "Difficulties: minimum ${sortedDifficulties.min()}, " +
                    "average ${sortedDifficulties.average()}, " +
                    "maximum ${sortedDifficulties.max()}"
            }
            logger.info {
                "Singles: minimum ${sortedSingles.min()}, " +
                    "average ${sortedSingles.average()}, " +
                    "maximum ${sortedSingles.max()}"
            }

            sortedSingles.groupingBy { it }.eachCount()
                .forEach { (singles, count) ->
                    logger.info { "singles $singles: $count" }
                }
        }

        private suspend fun calculateDifficulties(size: Int): List<Deferred<Pair<Double, Int>>> =
            kotlinx.coroutines.coroutineScope {
                val deferreds = mutableListOf<Deferred<Pair<Double, Int>>>()

                val variant =
                    GameVariant(
                        GridSize(size, size),
                        GameOptionsVariant(
                            true,
                            GridCageOperation.OPERATIONS_ALL,
                            DigitSetting.FIRST_DIGIT_ONE,
                            DifficultySetting.ANY,
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

        private suspend fun calculateOneDifficulty(creator: MergingCageGridCalculator): Pair<Double, Int> {
            val grid = creator.calculate()

            val pair =
                Pair(
                    GridDifficultyCalculator(grid).calculate(),
                    grid.cages.count { it.cells.size == 1 },
                )

            logger.info { "finished $pair" }

            return pair
        }
    }
}
