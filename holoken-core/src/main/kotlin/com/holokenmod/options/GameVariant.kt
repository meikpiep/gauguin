package com.holokenmod.options

import com.holokenmod.grid.GridSize

data class GameVariant(
    val gridSize: GridSize,
    val options: GameOptionsVariant
) {

    val width = gridSize.width
    val height = gridSize.height
    val surfaceArea = gridSize.surfaceArea

    val possibleDigits: Set<Int> by lazy {
        options
            .digitSetting
            .getPossibleDigits(gridSize)
    }

    val maximumDigit: Int
        get() = options
            .digitSetting
            .getMaximumDigit(gridSize)

    val possibleNonZeroDigits: Collection<Int> by lazy {
        options
            .digitSetting
            .getPossibleNonZeroDigits(gridSize)
    }
}
