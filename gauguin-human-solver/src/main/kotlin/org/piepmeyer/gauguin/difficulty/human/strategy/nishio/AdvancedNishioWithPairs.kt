package org.piepmeyer.gauguin.difficulty.human.strategy.nishio

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategyResult
import org.piepmeyer.gauguin.grid.Grid

private val logger = KotlinLogging.logger {}

/**
 * This is the same nihio solver than NishioWithPairs, with one difference: The initial possible
 * cell and candidate do not have two possibles, but more than two.
 *
 */
class AdvancedNishioWithPairs : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): HumanSolverStrategyResult {
        val possiblesCache = PossiblesCacheByCageNumber(grid, cache)

        grid.cells
            .filter { it.possibles.size > 2 }
            .forEach { cell ->
                cell.possibles.forEach { possible ->
                    val nishioCore = NishioCore(grid, possiblesCache, cell, possible)
                    val result = nishioCore.tryWithNishio()

                    if (result.hasFindings()) {
                        logger.info {
                            "Using advanced nishio on cell ${cell.cellNumber} with possible $possible, result is ${result::class.simpleName}."
                        }

                        val changedCells = nishioCore.applyFindings(result)

                        return HumanSolverStrategyResult.Success(changedCells)
                    }
                }
            }

        return HumanSolverStrategyResult.NothingChanged()
    }
}
