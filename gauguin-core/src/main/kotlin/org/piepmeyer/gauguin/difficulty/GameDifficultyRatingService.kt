package org.piepmeyer.gauguin.difficulty

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.GameVariant

class GameDifficultyRatingService {
    private val rater: GameDifficultyRater by lazy { GameDifficultyRater() }

    fun difficultyRating(variant: GameVariant): GameDifficultyRating? = rater.byVariant(variant)

    fun difficultyOfGrid(grid: Grid): DifficultySetting? = rater.difficulty(grid)

    fun isSupported(variant: GameVariant): Boolean = rater.isSupported(variant)
}
