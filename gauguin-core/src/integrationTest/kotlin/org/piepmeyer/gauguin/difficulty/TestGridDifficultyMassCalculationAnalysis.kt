package org.piepmeyer.gauguin.difficulty

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.resource.resourceAsString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.piepmeyer.gauguin.creation.GameVariantMassDifficultyItem
import java.io.File
import kotlin.math.roundToInt

class TestGridDifficultyMassCalculationAnalysis : FunSpec({
    xtest("calculateValues") {
        val calculatedDifficulties = resourceAsString("/org/piepmeyer/gauguin/difficulty/mass-difficulties.yml")

        val entries = Json.decodeFromString<List<GameVariantMassDifficultyItem>>(calculatedDifficulties)

        println("entries: ${entries.size}")

        val difficultyRatings = entries.map { entry ->
            val difficulties = entry.calculatedDifficulties

            val easy = round(difficulties[50])
            val medium = round(difficulties[332])
            val hart = round(difficulties[666])
            val extreme = round(difficulties[950])

            GameDifficultyRating(GameDifficultyVariant.fromGameVariant(entry.variant), easy, medium, hart, extreme)
        }

        val result = Json { prettyPrint = true }.encodeToString(difficultyRatings)

        File("difficulty-ratings.yml").writeText(result)
    }
})

fun round(value: Double): Double {
    return (value * 100).roundToInt() / 100.0
}
