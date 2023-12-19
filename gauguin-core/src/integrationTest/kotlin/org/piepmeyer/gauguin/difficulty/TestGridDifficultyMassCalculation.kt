package org.piepmeyer.gauguin.difficulty

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.piepmeyer.gauguin.creation.GameVariantMassDifficultyItem
import org.piepmeyer.gauguin.creation.GridCalculator
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.options.SingleCageUsage
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class TestGridDifficultyMassCalculation : FunSpec({
    xtest("calculateValues") {
        runBlocking(Dispatchers.Default) {

            DebugProbes.install()

            val groupedItems = calculateDifficulties()
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

//            println(result)

            File("temp.yml").writeText(result)
        }
    }
})

suspend fun calculateDifficulties(): List<Deferred<Pair<GameVariant, Double>>> = kotlinx.coroutines.coroutineScope {
    val digitSetting = DigitSetting.FIRST_DIGIT_ONE
    val deferreds = mutableListOf<Deferred<Pair<GameVariant, Double>>>()

    for (size in listOf(9)) {
        for (showOperators in listOf(true)) {
            for (cageOperation in GridCageOperation.entries) {
                for (singleCageUsage in listOf(SingleCageUsage.FIXED_NUMBER)) {
                    val variant = GameVariant(
                        GridSize(size, size),
                        GameOptionsVariant(
                            showOperators,
                            cageOperation,
                            digitSetting,
                            DifficultySetting.ANY,
                            singleCageUsage,
                            NumeralSystem.Decimal
                        )
                    )

                    val creator = GridCalculator(variant)

                    for (i in 0..999) {
                        deferreds += async(CoroutineName(variant.toString())) {
                            calculateOneDifficulty(variant, creator)
                        }
                    }
                }
            }
        }
    }

    return@coroutineScope deferreds
}

@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun calculateOneDifficulty(
    variant: GameVariant,
    creator: GridCalculator
): Pair<GameVariant, Double> {
    println("starting variant $variant")

    val grid = creator.calculate()

    val pair = Pair(
        variant,
        GridDifficultyCalculator(grid).calculate()
    )

    println("finishing variant $variant")

    // DebugProbes.dumpCoroutines()

    return pair
}
