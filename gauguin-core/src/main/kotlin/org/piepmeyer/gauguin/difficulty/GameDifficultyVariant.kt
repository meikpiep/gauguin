package org.piepmeyer.gauguin.difficulty

import kotlinx.serialization.Serializable
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.GridCageOperation
import org.piepmeyer.gauguin.options.SingleCageUsage

@Serializable
data class GameDifficultyVariant(
    val gridSize: GridSize,
    var showOperators: Boolean,
    var cageOperation: GridCageOperation,
    var digitSetting: DigitSetting,
    var singleCageUsage: SingleCageUsage
) {

    companion object {
        fun fromGameVariant(variant: GameVariant): GameDifficultyVariant {
            return GameDifficultyVariant(
                gridSize = variant.gridSize,
                cageOperation = variant.options.cageOperation,
                showOperators = variant.options.showOperators,
                digitSetting = variant.options.digitSetting,
                singleCageUsage = variant.options.singleCageUsage
            )
        }
    }
}
