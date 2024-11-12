package org.piepmeyer.gauguin.difficulty.human

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

private val logger = KotlinLogging.logger {}

class HumanSolver(
    private val grid: Grid,
    private val validate: Boolean = false,
) {
    private val humanSolverStrategy =
        HumanSolverStrategies.entries

    private val cache = PossiblesCache(grid)

    fun solveAndCalculateDifficulty(): HumanSolverResult {
        var progress: HumanSolverStep
        var success = true
        var difficulty = FillSingleCage().fillCells(grid) * 1

        cache.initialize()

        do {
            cache.validateEntries()
            progress = doProgress()

            if (progress.success) {
                difficulty += progress.difficulty
            } else if (!grid.isSolved()) {
                success = false

                logger.info { "Sad about grid:\n$grid" }
            }
        } while (progress.success && !grid.isSolved())

        return HumanSolverResult(success, difficulty)
    }

    private fun doProgress(): HumanSolverStep {
        humanSolverStrategy.forEach {
            val progress = it.solver.fillCells(grid, cache)

            if (progress) {
                logger.info { "Added ${it.difficulty} from ${it.solver::class.simpleName}" }

                if (validate &&
                    (grid.numberOfMistakes() != 0 || grid.cells.any { !it.isUserValueSet && it.possibles.isEmpty() })
                ) {
                    logger.error { "Last step introduced errors." }
                    throw IllegalStateException("Found a grid with wrong values.")
                }

                return HumanSolverStep(true, it.difficulty)
            }
        }

        return HumanSolverStep(false, 0)
    }

    fun prepareGrid() {
        grid.cells.forEach {
            it.possibles = grid.variant.possibleDigits
            it.userValue = GridCell.NO_VALUE_SET
        }
    }
}
