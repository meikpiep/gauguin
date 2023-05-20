package com.holokenmod.calculation

import com.holokenmod.creation.GridCalculator
import com.holokenmod.grid.Grid
import com.holokenmod.options.GameVariant
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.function.Function

class GridPreviewCalculationService {
    private val grids = mutableMapOf<GameVariant, Grid>()
    fun getOrCreateGrid(variant: GameVariant): Future<Grid> {
        val future = FutureTask { grids.computeIfAbsent(variant, computeVariant()) }
        val thread = Thread(future)
        thread.name = "PreviewCalculatorFromNew-" + variant.width + "x" + variant.height
        thread.start()
        return future
    }

    private fun computeVariant(): Function<GameVariant, Grid> {
        return Function { variant: GameVariant ->
            val creator = GridCalculator(variant)
            creator.calculate()
        }
    }

    fun getGrid(gameVariant: GameVariant): Grid? {
        return grids[gameVariant]
    }
}
