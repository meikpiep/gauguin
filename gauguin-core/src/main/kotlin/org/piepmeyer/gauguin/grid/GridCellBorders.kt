package org.piepmeyer.gauguin.grid

class GridCellBorders(
    var north: GridBorderType = GridBorderType.BORDER_NONE,
    var east: GridBorderType = GridBorderType.BORDER_NONE,
    var south: GridBorderType = GridBorderType.BORDER_NONE,
    var west: GridBorderType = GridBorderType.BORDER_NONE
) {
    fun resetBorders() {
        north = GridBorderType.BORDER_NONE
        east = GridBorderType.BORDER_NONE
        south = GridBorderType.BORDER_NONE
        west = GridBorderType.BORDER_NONE
    }
}
