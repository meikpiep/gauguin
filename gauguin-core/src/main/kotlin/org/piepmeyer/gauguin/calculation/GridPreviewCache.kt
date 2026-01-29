package org.piepmeyer.gauguin.calculation

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.GameVariant
import java.util.WeakHashMap

class GridPreviewCache {
    private val grids: MutableMap<GameVariant, Grid> = WeakHashMap()

    fun getGrid(gameVariant: GameVariant): Grid? = grids[gameVariant]

    fun putGrid(grid: Grid) {
        grids[grid.variant] = grid
    }

    fun clear() {
        grids.clear()
    }
}
