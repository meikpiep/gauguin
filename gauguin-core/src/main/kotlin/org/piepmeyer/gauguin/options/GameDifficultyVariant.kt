package org.piepmeyer.gauguin.options

import org.piepmeyer.gauguin.grid.GridSize

data class GameDifficultyVariant(
    val gridSize: GridSize,
    var showOperators: Boolean,
    var cageOperation: GridCageOperation,
    var digitSetting: DigitSetting,
    var singleCageUsage: SingleCageUsage
) {

    companion object {
        fun fromGameOptionsVariant(variant: GameVariant): GameDifficultyVariant {
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
