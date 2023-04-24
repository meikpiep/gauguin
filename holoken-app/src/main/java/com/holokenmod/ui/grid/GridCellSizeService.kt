package com.holokenmod.ui.grid

class GridCellSizeService {
    var cellSizePercent = 100
        set(cellSizePercent) {
            field = cellSizePercent
            listener?.cellSizeChanged(cellSizePercent)
        }

    private var listener: GridCellSizeListener? = null

    fun setCellSizeListener(listener: GridCellSizeListener?) {
        this.listener = listener
    }
}