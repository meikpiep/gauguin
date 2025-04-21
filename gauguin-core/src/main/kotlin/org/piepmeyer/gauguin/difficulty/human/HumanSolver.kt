package org.piepmeyer.gauguin.difficulty.human

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.grid.Grid
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
    private var revealedCells = 0

    private val difficultyUnsolvedCell = 1000

    fun solveAndCalculateDifficulty(avoidReveal: Boolean = false): HumanSolverResult {
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
                difficulty += difficultyUnsolvedCell
                success = false

                if (avoidReveal) {
                    break
                }

                revealUnsolvedCell()
            }
        } while (!grid.isSolved())

        solverDurations.entries
            .toSet()
            .associate { Pair(it.value, it.key) }
            .toSortedMap({ duration: Duration, otherDuration: Duration ->
                duration.compareTo(otherDuration) * -1
            })
            .forEach { (duration, solverClass) ->
                logger.debug { "sum $duration of ${solverClass.simpleName}" }
            }

        logger.debug { "Calculated difficulty of $difficulty, revealed $revealedCells cells." }

        return HumanSolverResult(success, difficulty)
    }

    private fun revealUnsolvedCell() {
        logger.debug { "Revealing one cell." }
        val firstCellWithMinimumPossibles =
            grid.cells
                .filter { !it.isUserValueSet }
                .sortedBy { it.possibles.size }
                .first()

        grid.setUserValueAndRemovePossibles(
            firstCellWithMinimumPossibles,
            firstCellWithMinimumPossibles.value,
        )

        revealedCells++
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
            it.userValue = null
        }
    }
}
