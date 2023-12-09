package org.piepmeyer.gauguin.calculation

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.piepmeyer.gauguin.creation.GridCalculator
import org.piepmeyer.gauguin.creation.GridCreator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.GameVariant
import java.util.WeakHashMap

private val logger = KotlinLogging.logger {}

class GridPreviewCalculationService {
    private val grids: MutableMap<GameVariant, Grid> = WeakHashMap()
    private var listeners = mutableListOf<GridPreviewListener>()
    private var lastVariant: GameVariant? = null
    private var lastGridCalculation: Deferred<Grid>? = null

    fun getGrid(gameVariant: GameVariant): Grid? {
        return grids[gameVariant]
    }

    fun calculateGrid(
        variant: GameVariant,
        scope: CoroutineScope,
    ) {
        if (lastVariant == variant) {
            return
        }

        lastVariant = variant

        var grid: Grid
        var previewStillCalculating: Boolean

        scope.launch(Dispatchers.Default) {
            logger.info { "Generating real grid..." }

            lastGridCalculation?.cancel()
            val gridCalculation = async { getOrCreateGrid(variant) }
            lastGridCalculation = gridCalculation

            val gridAfterShortTimeout = withTimeoutOrNull(250) { gridCalculation.await() }

            if (gridAfterShortTimeout == null) {
                logger.info { "Generating pseudo grid..." }
                val variantWithoutDifficulty =
                    variant.copy(
                        options = variant.options.copy(difficultySetting = DifficultySetting.ANY),
                    )

                grid = GridCreator(variantWithoutDifficulty).createRandomizedGridWithCages()
                previewStillCalculating = true
                logger.info { "Finished generating pseudo grid." }
            } else {
                logger.info { "Generated real grid with short timeout." }
                grid = gridAfterShortTimeout
                previewStillCalculating = false
            }

            listeners.forEach { it.previewGridCreated(grid, previewStillCalculating) }

            if (previewStillCalculating) {
                launch {
                    val calculatedGrid = gridCalculation.await()
                    listeners.forEach { it.previewGridCalculated(calculatedGrid) }
                }
            }
        }
    }

    private suspend fun getOrCreateGrid(variant: GameVariant): Grid {
        grids[variant]?.let {
            return it
        }

        val grid = GridCalculator(variant).calculate()
        grids[variant] = grid

        return grid
    }

    fun addListener(listener: GridPreviewListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: GridPreviewListener) {
        listeners.remove(listener)
    }
}
