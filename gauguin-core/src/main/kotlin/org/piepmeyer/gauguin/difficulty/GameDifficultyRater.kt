package org.piepmeyer.gauguin.difficulty

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.GameVariant

class GameDifficultyRater {
    private val difficultyLoader = GameDifficultyLoader.loadDifficulties()

    fun difficulty(grid: Grid): DifficultySetting? = difficulty(grid, GridDifficultyCalculator(grid).calculate())

    fun difficulty(
        gameRating: GameDifficultyRating?,
        grid: Grid,
    ): DifficultySetting? = difficulty(gameRating, GridDifficultyCalculator(grid).calculate())

    private fun difficulty(
        grid: Grid,
        difficultyValue: Double,
    ): DifficultySetting? = difficulty(difficultyLoader.byVariant(grid.variant), difficultyValue)

    private fun difficulty(
        gameRating: GameDifficultyRating?,
        difficultyValue: Double,
    ): DifficultySetting? = gameRating?.getDifficulty(difficultyValue)

    fun isSupported(variant: GameVariant): Boolean = difficultyLoader.isSupported(variant)

    fun byVariant(variant: GameVariant): GameDifficultyRating? = difficultyLoader.byVariant(variant)
}
