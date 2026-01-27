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
import org.piepmeyer.gauguin.difficulty.human.HumanDifficultyCalculatorImpl
import org.piepmeyer.gauguin.game.save.SaveGame
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.options.SingleCageUsage
import java.io.File

private val logger = KotlinLogging.logger {}

class TestChallengeCreator :
    FunSpec({
        xtest("calculateValues 4x4") {
            testManyGrids(4)
        }

        xtest("calculateValues 5x5") {
            testManyGrids(5)
        }

        xtest("calculateValues 6x6") {
            testManyGrids(6)
        }

        xtest("calculateValues 9x9") {
            testManyGrids(9)
        }
    }) {
    companion object {
        private fun testManyGrids(size: Int) {
            val grids =
                runBlocking(Dispatchers.Default) {
                    calculateDifficulties(size).awaitAll()
                }

            val easiestGrid = grids.minBy { it.difficulty.humanDifficulty!! }
            val hardestGrid =
                grids
                    .filter {
                        it.difficulty.solvedViaHumanDifficulty == true &&
                            it.difficulty.solvedViaHumanDifficultyIncludingNishio == false
                    }.maxBy { it.difficulty.humanDifficulty!! }

            logger.info {
                "Easiest grid: ${easiestGrid.difficulty.humanDifficulty} "
            }

            logger.info {
                "Hardest grid: ${hardestGrid.difficulty.humanDifficulty} "
            }

            saveGrid(easiestGrid, "${size}x$size-easiest.yaml")
            saveGrid(hardestGrid, "${size}x$size-hardest.yaml")
        }

        private fun saveGrid(
            grid: Grid,
            fileName: String,
        ) {
            grid.isActive = true
            val saveGame =
                SaveGame.createWithFile(
                    File(
                        SaveGame.SAVEGAME_NAME_PREFIX +
                            fileName,
                    ),
                )

            saveGame.save(grid)
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

                for (i in 0..9999) {
                    val randomizer = SeedRandomizerMock(i)
                    val creator = MergingCageGridCalculator(variant, randomizer, RandomPossibleDigitsShuffler(randomizer.random))

                    deferreds +=
                        async(CoroutineName(i.toString())) {
                            calculateOneDifficulty(
                                creator,
                            )
                        }
                }

                return@coroutineScope deferreds
            }

        private suspend fun calculateOneDifficulty(creator: MergingCageGridCalculator): Grid {
            val grid = creator.calculate()

            HumanDifficultyCalculatorImpl(grid, avoidNishioAndReveal = true).ensureDifficultyCalculated()

            logger.info { "finished ${grid.variant}" }

            return grid
        }
    }
}
