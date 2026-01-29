package org.piepmeyer.gauguin.calculation

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.piepmeyer.gauguin.creation.DifficultyAwareGridCreator
import org.piepmeyer.gauguin.creation.GridCalculatorFactory
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.SingleCageUsage

private val logger = KotlinLogging.logger {}

class GridPreviewCalculator(
    private val variant: GameVariant,
    private val listeners: MutableList<GridPreviewListener>,
    private val cache: GridPreviewCache,
    private val scope: CoroutineScope,
) {
    private var lastGridCalculation: Deferred<Grid>? = null

    suspend fun calculateGrid() {
        var grid: Grid
        var previewStillCalculating: Boolean

        logger.info { "Fetching real grid..." }

        val gridCalculation =
            scope.async(CoroutineName("GridPreview-calculation-$variant")) {
                createGrid(variant)
            }
        lastGridCalculation = gridCalculation

        val gridAfterShortTimeout = withTimeoutOrNull(250) { gridCalculation.await() }

        if (gridAfterShortTimeout == null) {
            logger.info { "Generating pseudo grid..." }
            val variantWithoutDifficulty = pseudoGridPreviewVariant(variant)

            grid =
                DifficultyAwareGridCreator(variantWithoutDifficulty).createRandomizedGridWithCages()
            previewStillCalculating = true
            logger.info { "Finished generating pseudo grid." }
        } else {
            logger.info { "Fetched real grid with short timeout." }
            grid = gridAfterShortTimeout
            previewStillCalculating = false
        }

        listeners.forEach { it.previewGridCreated(grid, previewStillCalculating) }

        if (previewStillCalculating) {
            scope.launch {
                val calculatedGrid = gridCalculation.await()
                listeners.forEach { it.previewGridCalculated(calculatedGrid) }
            }
        }
    }

    private suspend fun createGrid(variant: GameVariant): Grid {
        logger.debug { "Calculating grid..." }
        val grid = GridCalculatorFactory().createCalculator(variant).calculate()

        cache.putGrid(grid)
        logger.debug { "Grid calculated and stored." }

        return grid
    }

    fun cancelCalculation() {
        lastGridCalculation?.cancel()
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
