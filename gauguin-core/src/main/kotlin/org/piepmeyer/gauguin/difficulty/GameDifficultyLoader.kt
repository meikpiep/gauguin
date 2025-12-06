package org.piepmeyer.gauguin.difficulty

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.piepmeyer.gauguin.options.GameVariant

@OptIn(ExperimentalSerializationApi::class)
class GameDifficultyLoader private constructor() {
    private val ratings: List<GameDifficultyRating>

    init {
        val ratingFileStream =
            this::class.java
                .getResourceAsStream("/org/piepmeyer/gauguin/difficulty/difficulty-ratings.yml")!!

        ratings = Json.decodeFromStream<List<GameDifficultyRating>>(ratingFileStream)
    }

    fun byVariant(variant: GameVariant): GameDifficultyRating? {
        val variantWithAnyDifficulty = GameDifficultyVariant.fromGameVariant(variant)

        return ratings.firstOrNull { it.variant == variantWithAnyDifficulty }
    }

    fun isSupported(variant: GameVariant): Boolean = byVariant(variant) != null

    companion object {
        fun loadDifficulties(): GameDifficultyLoader = GameDifficultyLoader()
    }
}
