package com.holokenmod.options

import com.holokenmod.grid.GridSize

data class GameVariant(
    val gridSize: GridSize,
    val options: GameOptionsVariant
) {

    val width = gridSize.width
    val height = gridSize.height
    val surfaceArea = gridSize.surfaceArea
}
