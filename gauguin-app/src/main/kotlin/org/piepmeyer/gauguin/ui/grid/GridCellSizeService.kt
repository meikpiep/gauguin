package org.piepmeyer.gauguin.ui.grid

class GridCellSizeService {
    private var listener: GridCellSizeListener? = null

    fun setCellSizeListener(listener: GridCellSizeListener?) {
        this.listener = listener
    }
}
