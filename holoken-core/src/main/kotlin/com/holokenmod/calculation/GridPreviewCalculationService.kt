package com.holokenmod.calculation

import com.holokenmod.creation.GridCalculator
import com.holokenmod.grid.Grid
import com.holokenmod.options.GameVariant
import java.util.WeakHashMap

class GridPreviewCalculationService {
    private val grids: MutableMap<GameVariant, Grid> = WeakHashMap()

    suspend fun getOrCreateGrid(variant: GameVariant): Grid {
        grids[variant]?.let {
            return it
        }

        val grid = GridCalculator(variant).calculate()
        grids[variant] = grid

        return grid
    }

    fun getGrid(gameVariant: GameVariant): Grid? {
        return grids[gameVariant]
    }
}
