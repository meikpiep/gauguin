package com.holokenmod.calculation

import com.holokenmod.grid.Grid

interface GridPreviewListener {

    fun previewGridCalculated(grid: Grid)
    fun previewGridCreated(grid: Grid, previewStillCalculating: Boolean)
}
