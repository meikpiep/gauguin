package org.piepmeyer.gauguin.difficulty.human

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.measureTimedValue

private val logger = KotlinLogging.logger {}

class HumanSolver(
    private val grid: Grid,
    private val validate: Boolean = false,
) {
    private val humanSolverStrategy =
        HumanSolverStrategies.entries

    private val cache = HumanSolverCache(grid)

    private val solverDurations = mutableMapOf<KClass<out HumanSolverStrategy>, Duration>()

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

        solverDurations.forEach { solverClass, duration ->
            logger.debug { "sum of ${solverClass.simpleName} is $duration" }
        }

        return HumanSolverResult(success, difficulty)
    }

    private fun doProgress(): HumanSolverStep {
        humanSolverStrategy.forEach {
            val measuredTimedValue =
                measureTimedValue {
                    it.solver.fillCells(grid, cache)
                }

            val oldDuration = solverDurations[it.solver::class] ?: Duration.ZERO

            solverDurations[it.solver::class] = oldDuration + measuredTimedValue.duration

            logger.debug { "Invoked ${it.solver::class.simpleName}, duration ${measuredTimedValue.duration}" }

            if (measuredTimedValue.value) {
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
