package org.piepmeyer.gauguin.calculation

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import org.piepmeyer.gauguin.creation.GridCalculatorFactory
import org.piepmeyer.gauguin.difficulty.ensureDifficultyCalculated
import org.piepmeyer.gauguin.difficulty.human.HumanDifficultyCalculatorFactory
import org.piepmeyer.gauguin.game.save.SavedGamesService
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.GameVariant

private val logger = KotlinLogging.logger {}

enum class NextGridState {
    CALCULATED,
    CURRENTLY_CALCULATING,
}

class GridCalculationService(
    var variant: GameVariant,
    @InjectedParam private val savedGamesService: SavedGamesService,
    @InjectedParam private val humanDifficultyFactory: HumanDifficultyCalculatorFactory,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : KoinComponent {
    private val fileNameNextGrid = "next-grid.json"

    val listeners = mutableListOf<GridCalculationListener>()
    private var currentGridJob: Job? = null
    private var nextGrid: Grid? = null
    private var nextGridJob: Job? = null
    private val nextGridSemaphore = Semaphore(1)

    private val mutableNextGridState = MutableStateFlow(NextGridState.CALCULATED)

    val nextGridState: StateFlow<NextGridState> = mutableNextGridState.asStateFlow()

    fun addListener(listener: GridCalculationListener) {
        listeners += listener
    }

    suspend fun calculateCurrentGrid(
        variant: GameVariant,
        scope: CoroutineScope,
        invokeAfterNewGridWasCreated: (Grid) -> Unit,
    ) {
        nextGridSemaphore.withPermit {
            nextGrid = null
        }
        this.variant = variant
        calculateCurrentGrid(scope, invokeAfterNewGridWasCreated)
    }

    private fun calculateCurrentGrid(
        scope: CoroutineScope,
        invokeAfterNewGridWasCreated: (Grid) -> Unit,
    ) {
        currentGridJob =
            scope.launch(dispatcher) {
                logger.info { "Calculating current grid of $variant" }
                listeners.forEach { it.startingCurrentGridCalculation() }

                logger.info { "Calculating current grid via factory of $variant" }
                val newGrid = GridCalculatorFactory().createCalculator(variant).calculate()
                invokeAfterNewGridWasCreated.invoke(newGrid)
                logger.info { "Finished calculating current grid via factory of $variant" }

                listeners.forEach { it.currentGridCalculated() }
                logger.info { "Finished calculating current grid of $variant" }
            }
    }

    fun calculateNextGrid(scope: CoroutineScope) {
        nextGridJob =
            scope.launch(dispatcher) {
                logger.info { "Calculating next grid of $variant" }
                mutableNextGridState.value = NextGridState.CURRENTLY_CALCULATING

                logger.info { "Calculating next grid via factory of $variant" }

                val grid = GridCalculatorFactory().createCalculator(variant).calculate()

                nextGridSemaphore.withPermit {
                    nextGrid = grid
                    logger.info { "Calculating difficulty of next grid" }
                    grid.ensureDifficultyCalculated()
                    humanDifficultyFactory.createCalculator(grid).ensureDifficultyCalculated()

                    saveNextGrid()
                }

                logger.info { "Finished calculating next grid via factory of $variant" }
                mutableNextGridState.value = NextGridState.CALCULATED
                logger.info { "Finished calculating next grid of $variant" }
            }
    }

    private fun saveNextGrid() {
        logger.info { "Saving next grid." }
        savedGamesService.saveGrid(nextGrid!!, fileNameNextGrid)
    }

    suspend fun loadNextGrid() {
        nextGridSemaphore.withPermit {
            val loadedGrid = savedGamesService.loadGrid(fileNameNextGrid)

            loadedGrid?.let {
                logger.info { "Found stored next grid." }

                nextGrid = it
            }
        }
    }

    fun hasCalculatedNextGrid(variantParam: GameVariant): Boolean = nextGrid != null && variantParam == variant

    suspend fun consumeNextGrid(): Grid {
        nextGridSemaphore.withPermit {
            val grid = nextGrid!!
            nextGrid = null

            logger.info { "Deleting stored next grid." }
            savedGamesService.deleteGame(fileNameNextGrid)

            return grid
        }
    }

    suspend fun setNextGrid(grid: Grid) {
        nextGridSemaphore.withPermit {
            nextGrid = grid
        }
    }

    fun stopCalculations() {
        currentGridJob?.cancel(message = "Grid changed.")
        nextGridJob?.cancel(message = "Grid changed.")
    }
}
