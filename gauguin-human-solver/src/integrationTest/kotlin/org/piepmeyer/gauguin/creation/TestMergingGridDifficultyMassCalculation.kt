package org.piepmeyer.gauguin.creation

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.piepmeyer.gauguin.difficulty.GameDifficultyVariant
import org.piepmeyer.gauguin.difficulty.human.HumanDifficultyCalculatorImpl
import org.piepmeyer.gauguin.difficulty.human.HumanGameVariantMassDifficultyItem
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.options.SingleCageUsage
import java.io.File

class TestMergingGridDifficultyMassCalculation :
    FunSpec({
        test("calculateValues") {
            runBlocking(Dispatchers.Default) {

                val groupedItems =
                    calculateDifficulties()
                        .map {
                            println("waiting for $it")
                            val value = it.await()
                            println("finished: $it")
                            value
                        }.groupBy({ it.first }, { it.second })
                        .map {
                            HumanGameVariantMassDifficultyItem(it.key, it.value.sorted())
                        }

                println("calculated difficulties ${groupedItems.size}.")

                val result = Json { prettyPrint = true }.encodeToString(groupedItems)

                File("mass-difficulties-merging-human.yml").writeText(result)
            }
        }
    }) {
    companion object {
        suspend fun calculateDifficulties(): List<Deferred<Pair<GameDifficultyVariant, Int>>> =
            coroutineScope {
                val deferreds = mutableListOf<Deferred<Pair<GameDifficultyVariant, Int>>>()

                for (size in listOf(7)) {
                    // for (digitSetting in DigitSetting.entries) {
                    // for (showOperators in listOf(true, false)) {
                    // for (cageOperation in GridCageOperation.entries) {
                    val variant =
                        GameVariant(
                            GridSize(size, size),
                            GameOptionsVariant(
                                true,
                                GridCageOperation.OPERATIONS_ALL,
                                DigitSetting.FIRST_DIGIT_ONE,
                                setOf(DifficultySetting.EXTREME),
                                SingleCageUsage.NO_SINGLE_CAGES,
                                NumeralSystem.Decimal,
                            ),
                        )

                    val creator = MergingCageGridCalculator(variant)

                    (0..999).forEach { index ->
                        deferreds +=
                            async(CoroutineName(index.toString())) {
                                calculateOneDifficulty(
                                    GameDifficultyVariant.Companion.fromGameVariant(
                                        variant,
                                    ),
                                    creator,
                                )
                            }
                    }
                    // }
                    // }
                    // }
                }

                return@coroutineScope deferreds
            }

        private suspend fun calculateOneDifficulty(
            variant: GameDifficultyVariant,
            creator: MergingCageGridCalculator,
        ): Pair<GameDifficultyVariant, Int> {
            println("starting variant $variant")

            val grid = creator.calculate()
            HumanDifficultyCalculatorImpl(grid).ensureDifficultyCalculated()

            val pair = Pair(variant, grid.difficulty.humanDifficulty!!)

            println("finishing variant $variant")

            return pair
        }
    }
}
