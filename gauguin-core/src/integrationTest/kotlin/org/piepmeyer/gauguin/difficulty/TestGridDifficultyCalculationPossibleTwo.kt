package org.piepmeyer.gauguin.difficulty

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.piepmeyer.gauguin.creation.GameVariantMassDifficultyItem
import org.piepmeyer.gauguin.creation.GridCalculator
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.NumeralSystem
import java.io.File

class TestGridDifficultyCalculationPossibleTwo : FunSpec({
    xtest("calculateValues") {
        runBlocking(Dispatchers.Default) {

            val fileData =
                this::class.java
                    .getResource("/org/piepmeyer/gauguin/difficulty/possibles-to-10.yml")!!
                    .readText()

            val possibles = Json.decodeFromString<List<GameVariantPossibleItem>>(fileData)

            val groupedItems =
                calculateDifficulties(
                    possibles.filter { it.calculatedDifficulties == 10 }.map { it.variant },
                )
                    .map {
                        println("waiting for $it")
                        val value = it.await()
                        println("finished: $it")
                        value
                    }
                    .groupBy({ it.first }, { it.second })
                    .map {
                        GameVariantMassDifficultyItem(it.key, it.value.sorted())
                    }

            println("calculated difficulties ${groupedItems.size}.")

            val result = Json { prettyPrint = true }.encodeToString(groupedItems)

            File("mass-difficulties-10-10.yml").writeText(result)
        }
    }
}) {
    companion object {
        suspend fun calculateDifficulties(variants: List<GameDifficultyVariant>): List<Deferred<Pair<GameDifficultyVariant, Double>>> =
            kotlinx.coroutines.coroutineScope {
                val deferreds = mutableListOf<Deferred<Pair<GameDifficultyVariant, Double>>>()

                variants.forEach {
                    val variant =
                        GameVariant(
                            it.gridSize,
                            GameOptionsVariant(
                                it.showOperators,
                                it.cageOperation,
                                it.digitSetting,
                                DifficultySetting.ANY,
                                it.singleCageUsage,
                                NumeralSystem.Decimal,
                            ),
                        )

                    val creator = GridCalculator(variant)

                    for (i in 0..999) {
                        deferreds +=
                            async(CoroutineName(it.toString())) {
                                calculateOneDifficulty(
                                    it,
                                    creator,
                                )
                            }
                    }
                }

                return@coroutineScope deferreds
            }

        private suspend fun calculateOneDifficulty(
            variant: GameDifficultyVariant,
            creator: GridCalculator,
        ): Pair<GameDifficultyVariant, Double> {
            println("starting variant $variant")

            val grid = creator.calculate()

            val pair =
                Pair(
                    variant,
                    GridDifficultyCalculator(grid).calculate(),
                )

            println("finishing variant $variant")

            return pair
        }
    }
}
