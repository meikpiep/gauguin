package org.piepmeyer.gauguin.difficulty.human

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

private val logger = KotlinLogging.logger {}

class HumanSolver(
    private val grid: Grid,
    private val validate: Boolean = false,
    avoidNishio: Boolean = false,
) {
    private val humanSolverStrategy: List<HumanSolverStrategies> =
        if (avoidNishio) {
            HumanSolverStrategies.entries.filter { !it.isNishio }
        } else {
            HumanSolverStrategies.entries
        }

    private val cache = HumanSolverCacheImpl(grid)
    private var changedCells: Collection<GridCell> = emptyList()

    private var revealedCells = 0
    private var usedNishio = false

    private val difficultyUnsolvedCell = 1000

    fun solveAndCalculateDifficulty(avoidReveal: Boolean = false): HumanSolverResult {
        var progress: HumanSolverStep
        var success = true
        var difficulty = FillSingleCages().fillCells(grid) * 1

        logger.info { "Starting human solver..." }

        cache.initialize()
        cache.validateAllEntries()

        do {
            if (changedCells.isNotEmpty()) {
                cache.validateEntries(changedCells)
            }

            progress = doProgress()

            if (progress.success) {
                difficulty += progress.difficulty
            } else if (!grid.isSolved()) {
                difficulty += difficultyUnsolvedCell
                success = false

                if (avoidReveal) {
                    break
                }

                revealUnsolvedCell()
            }
        } while (!grid.isSolved())

        logger.info { "Solver finished, difficulty of $difficulty, revealed $revealedCells cells." }

        return HumanSolverResult(success, usedNishio, difficulty)
    }

    private fun revealUnsolvedCell() {
        logger.debug { "Revealing one cell." }
        val firstCellWithMinimumPossibles =
            grid.cells
                .filter { !it.isUserValueSet }
                .minByOrNull { it.possibles.size }!!

        changedCells =
            grid.setUserValueAndRemovePossibles(
                firstCellWithMinimumPossibles,
                firstCellWithMinimumPossibles.value,
            )

        revealedCells++
    }

    private fun doProgress(): HumanSolverStep {
        humanSolverStrategy.forEach {
            val result = it.solver.fillCells(grid, cache)

            if (result is HumanSolverStrategyResult.Success) {
                if (it.isNishio) {
                    usedNishio = true
                }
                logger.trace { "Added ${it.difficulty} from ${it.solver::class.simpleName}" }
                changedCells = result.changedCells

                if (validate &&
                    (grid.numberOfMistakes() != 0 || grid.cells.any { !it.isUserValueSet && it.possibles.isEmpty() })
                ) {
                    logger.error { "Last step introduced errors." }
                    throw IllegalStateException("Found a grid with wrong values.")
                }

                return HumanSolverStep(true, it.difficulty, usedNishio)
            }
        }

        return HumanSolverStep(false, 0)
    }

    fun prepareGrid() {
        grid.cells.forEach {
            it.possibles = grid.variant.possibleDigits
            it.userValue = null
        }
    }
}
