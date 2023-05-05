package com.holokenmod.grid

import com.holokenmod.Direction

class GridCellBorders @JvmOverloads constructor(
    north: GridBorderType = GridBorderType.BORDER_NONE,
    east: GridBorderType = GridBorderType.BORDER_NONE,
    south: GridBorderType = GridBorderType.BORDER_NONE,
    west: GridBorderType = GridBorderType.BORDER_NONE
) {
    private var borders: MutableMap<Direction, GridBorderType> = mutableMapOf(
        Direction.NORTH to north,
                Direction.WEST to west,
                Direction.SOUTH to south,
                Direction.EAST to east,
    )

    fun getBorderType(direction: Direction): GridBorderType {
        return borders[direction]!!
    }

    fun setBorderType(direction: Direction, borderType: GridBorderType) {
        borders[direction] = borderType
    }
}