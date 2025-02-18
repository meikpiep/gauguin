package org.piepmeyer.gauguin.options

import org.piepmeyer.gauguin.grid.GridSize

data class GameVariant(
    val gridSize: GridSize,
    val options: GameOptionsVariant,
) {
    val width = gridSize.width
    val height = gridSize.height
    val surfaceArea = gridSize.surfaceArea

    val possibleDigits: Set<Int> by lazy {
        options
            .digitSetting
            .getPossibleDigits(gridSize)
    }

    val maximumDigit: Int =
        options
            .digitSetting
            .getMaximumDigit(gridSize)

    val possibleNonZeroDigits: Set<Int> by lazy {
        options
            .digitSetting
            .getPossibleNonZeroDigits(gridSize)
    }
}
