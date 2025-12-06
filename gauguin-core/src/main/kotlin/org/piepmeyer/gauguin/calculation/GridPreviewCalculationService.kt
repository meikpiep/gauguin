package org.piepmeyer.gauguin.calculation

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withTimeoutOrNull
import org.piepmeyer.gauguin.creation.DifficultyAwareGridCreator
import org.piepmeyer.gauguin.creation.GridCalculatorFactory
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.SingleCageUsage
import java.util.WeakHashMap

private val logger = KotlinLogging.logger {}

class GridPreviewCalculationService(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val grids: MutableMap<GameVariant, Grid> = WeakHashMap()
    private var listeners = mutableListOf<GridPreviewListener>()
    private var lastVariant: GameVariant? = null
    private var lastGridCalculation: Deferred<Grid>? = null

    fun getGrid(gameVariant: GameVariant): Grid? = grids[gameVariant]

    fun takeCalculatedGrid(grid: Grid) {
        grids[grid.variant] = grid
        lastVariant = grid.variant
        listeners.forEach { it.previewGridCreated(grid, false) }
    }

    fun calculateGrid(
        variant: GameVariant,
        scope: CoroutineScope,
    ) {
        if (lastVariant == variant) {
            return
        }

        lastVariant = variant
        lastGridCalculation?.cancel()

        var grid: Grid
        var previewStillCalculating: Boolean

        scope.launch(dispatcher) {
            with(this + CoroutineName("GridPreview-$variant")) {
                logger.info { "Fetching real grid..." }

                val gridCalculation = async(CoroutineName("GridPreview-calculation-$variant")) { getOrCreateGrid(variant) }
                lastGridCalculation = gridCalculation

                val gridAfterShortTimeout = withTimeoutOrNull(250) { gridCalculation.await() }

                if (gridAfterShortTimeout == null) {
                    logger.info { "Generating pseudo grid..." }
                    val variantWithoutDifficulty = pseudoGridPreviewVariant(variant)

                    grid = DifficultyAwareGridCreator(variantWithoutDifficulty).createRandomizedGridWithCages()
                    previewStillCalculating = true
                    logger.info { "Finished generating pseudo grid." }
                } else {
                    logger.info { "Fetched real grid with short timeout." }
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
    }

    private suspend fun getOrCreateGrid(variant: GameVariant): Grid {
        grids[variant]?.let {
            logger.debug { "Returning already calculated grid." }
            return it
        }

        logger.debug { "Calculating grid..." }
        val grid = GridCalculatorFactory().createCalculator(variant).calculate()
        grids[variant] = grid
        logger.debug { "Grid calculated and stored." }

        return grid
    }

    fun addListener(listener: GridPreviewListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: GridPreviewListener) {
        listeners.remove(listener)
    }

    fun clearGrids() {
        grids.clear()
        lastVariant = null
        lastGridCalculation = null
    }

    companion object {
        fun pseudoGridPreviewVariant(variant: GameVariant): GameVariant {
            val variantWithoutDifficulty =
                variant.copy(
                    options =
                        variant.options.copy(
                            difficultiesSetting = DifficultySetting.all(),
                            singleCageUsage =
                                if (variant.options.singleCageUsage == SingleCageUsage.NO_SINGLE_CAGES) {
                                    SingleCageUsage.DYNAMIC
                                } else {
                                    variant.options.singleCageUsage
                                },
                        ),
                )
            return variantWithoutDifficulty
        }
    }
}
