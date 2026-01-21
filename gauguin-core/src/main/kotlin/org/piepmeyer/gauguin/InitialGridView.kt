package org.piepmeyer.gauguin

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridView

class InitialGridView(
    private val initialGrid: Grid,
) : GridView {
    override var grid: Grid
        get() = initialGrid
        set(_) {
            // dummy implementation
        }

    override fun requestFocus() = false

    override fun invalidate() {
        // dummy implementation
    }
}
