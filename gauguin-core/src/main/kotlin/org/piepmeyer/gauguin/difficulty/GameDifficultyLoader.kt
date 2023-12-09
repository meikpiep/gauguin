package org.piepmeyer.gauguin.difficulty

import kotlinx.serialization.json.Json
import org.piepmeyer.gauguin.options.GameVariant

class GameDifficultyLoader private constructor() {
    private val ratings: List<GameDifficultyRating>

    init {
        val ratingFileContent =
            this::class.java
                .getResource("/org/piepmeyer/gauguin/difficulty/difficulty-ratings.yml")!!
                .readText()

        ratings = Json.decodeFromString<List<GameDifficultyRating>>(ratingFileContent)
    }

    fun byVariant(variant: GameVariant): GameDifficultyRating? {
        val variantWithAnyDifficulty = GameDifficultyVariant.fromGameVariant(variant)

        return ratings.firstOrNull { it.variant == variantWithAnyDifficulty }
    }

    fun isSupported(variant: GameVariant): Boolean {
        return byVariant(variant) != null
    }

    companion object {
        fun loadDifficulties(): GameDifficultyLoader {
            return GameDifficultyLoader()
        }
    }
}
