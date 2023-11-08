package org.piepmeyer.gauguin.options

import org.piepmeyer.gauguin.grid.GridSize

enum class GameDifficultyRatings(
    private val variant: GameDifficultyVariant,
    private val thresholdEasy: Double,
    private val thresholdMedium: Double,
    private val thresholdHard: Double,
    private val tresholdExtreme: Double
) {
    CLASSIC(
        GameDifficultyVariant.fromGameOptionsVariant(
            GameVariant(GridSize(9, 9), GameOptionsVariant.createClassic())
        ),
        70.40, 76.20, 80.80, 86.51
    ),
    CLASSIC_NO_SINGLE_CAGES(
        GameDifficultyVariant.fromGameOptionsVariant(
            GameVariant(GridSize(9, 9), GameOptionsVariant.createClassic().copy(singleCageUsage = SingleCageUsage.NO_SINGLE_CAGES))
        ),
        69.62, 76.08, 80.38, 86.70
    );

    fun getDifficulty(difficultyValue: Double): GameDifficulty {
        if (difficultyValue >= tresholdExtreme) {
            return GameDifficulty.EXTREME
        }
        if (difficultyValue >= thresholdHard) {
            return GameDifficulty.HARD
        }
        if (difficultyValue >= thresholdMedium) {
            return GameDifficulty.MEDIUM
        }
        return if (difficultyValue >= thresholdEasy) {
            GameDifficulty.EASY
        } else {
            GameDifficulty.VERY_EASY
        }
    }

    companion object {
        fun isSupported(variant: GameVariant): Boolean {
            return byVariant(variant) != null
        }

        fun byVariant(variant: GameVariant): GameDifficultyRatings? {
            val variantWithAnyDifficulty = GameDifficultyVariant.fromGameOptionsVariant(variant)

            return GameDifficultyRatings.values().firstOrNull { it.variant == variantWithAnyDifficulty }
        }
    }
}
