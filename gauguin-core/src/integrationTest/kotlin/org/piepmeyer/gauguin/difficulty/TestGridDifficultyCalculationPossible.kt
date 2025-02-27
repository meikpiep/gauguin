package org.piepmeyer.gauguin.difficulty

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.piepmeyer.gauguin.creation.RandomCageGridCalculator
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.options.SingleCageUsage
import java.io.File
import kotlin.time.Duration.Companion.minutes

private val logger = KotlinLogging.logger {}

class TestGridDifficultyCalculationPossible :
    FunSpec({
        xtest("calculateValues") {
            runBlocking(Dispatchers.Default) {

                val groupedItems =
                    calculateDifficulties()
                        .map {
                            logger.info { "waiting for $it" }
                            val value = it.await()
                            logger.info { "finished: $it" }
                            value
                        }.groupBy({ it.first }, { it.second })
                        .map {
                            val success = it.value.count { it }

                            val item = GameVariantPossibleItem(it.key, success)

                            logger.info { "Possible: $success, ${it.key}" }

                            item
                        }.sortedBy { it.calculatedDifficulties }

                logger.info { "calculated difficulties ${groupedItems.size}." }

                val result = Json { prettyPrint = true }.encodeToString(groupedItems)

                File("possibles.yml").writeText(result)
            }
        }
    }) {
    companion object {
        suspend fun calculateDifficulties(): List<Deferred<Pair<GameDifficultyVariant, Boolean>>> =
            kotlinx.coroutines.coroutineScope {
                val deferreds = mutableListOf<Deferred<Pair<GameDifficultyVariant, Boolean>>>()

                for (size in 8..11) {
                    for (digitSetting in DigitSetting.entries) {
                        for (showOperators in listOf(true, false)) {
                            for (cageOperation in GridCageOperation.entries) {
                                for (singleCageUsage in SingleCageUsage.entries) {
                                    val variant =
                                        GameVariant(
                                            GridSize(size, size),
                                            GameOptionsVariant(
                                                showOperators,
                                                cageOperation,
                                                digitSetting,
                                                DifficultySetting.ANY,
                                                singleCageUsage,
                                                NumeralSystem.Decimal,
                                            ),
                                        )

                                    val creator = RandomCageGridCalculator(variant)

                                    for (i in 0..9) {
                                        deferreds +=
                                            async(CoroutineName(variant.toString())) {
                                                calculateOneDifficulty(
                                                    GameDifficultyVariant.fromGameVariant(variant),
                                                    creator,
                                                )
                                            }
                                    }
                                }
                            }
                        }
                    }
                }

                return@coroutineScope deferreds
            }

        private suspend fun calculateOneDifficulty(
            variant: GameDifficultyVariant,
            creator: RandomCageGridCalculator,
        ): Pair<GameDifficultyVariant, Boolean> {
            logger.info { "starting variant $variant" }

            val grid =
                withTimeoutOrNull(
                    5.minutes,
                ) {
                    creator.calculate()
                }

            logger.info { "finishing variant $variant" }

            return Pair(variant, grid != null)
        }
    }
}
