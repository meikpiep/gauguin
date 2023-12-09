package org.piepmeyer.gauguin.difficulty

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.GameVariant

class GameDifficultyRater {
    private val difficultyLoader = GameDifficultyLoader.loadDifficulties()

    fun difficulty(grid: Grid): GameDifficulty {
        return difficulty(grid, GridDifficultyCalculator(grid).calculate())
    }

    fun difficulty(
        gameRating: GameDifficultyRating?,
        grid: Grid,
    ): GameDifficulty {
        return difficulty(gameRating, GridDifficultyCalculator(grid).calculate())
    }

    private fun difficulty(
        grid: Grid,
        difficultyValue: Double,
    ): GameDifficulty {
        return difficulty(difficultyLoader.byVariant(grid.variant), difficultyValue)
    }

    private fun difficulty(
        gameRating: GameDifficultyRating?,
        difficultyValue: Double,
    ): GameDifficulty {
        return gameRating?.getDifficulty(difficultyValue)
            ?: return GameDifficulty.VERY_EASY
    }

    fun isSupported(variant: GameVariant): Boolean {
        return difficultyLoader.isSupported(variant)
    }

    fun byVariant(variant: GameVariant): GameDifficultyRating? {
        return difficultyLoader.byVariant(variant)
    }
}
