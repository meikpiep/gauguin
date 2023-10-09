package org.piepmeyer.gauguin.calculation

import org.piepmeyer.gauguin.grid.Grid

interface GridPreviewListener {

    fun previewGridCalculated(grid: Grid)
    fun previewGridCreated(grid: Grid, previewStillCalculating: Boolean)
}
