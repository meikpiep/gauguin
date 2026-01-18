package org.piepmeyer.gauguin.difficulty.human.strategy.nishio

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategyResult
import org.piepmeyer.gauguin.grid.Grid

/**
 * Uses nishio (guessing) with cells containing two possible numbers.
 *
 * For all cells with two possible numbers, each number is used to utilize nishio with the given
 * number. If the algorithm finds
 *   - a contradiction (meaning the conclusions from this nishio try lead to an impossible state)
 *     --> the other possible number gets put into the cell
 *   - all remaining cells can be filled without a contradiction
 *     --> we found the solution of the entire grid, put in the values, the grid was solved
 *
 * One possible nishio cell with one possible value is executed as follow:
 *   - put in the possible number into the nishio cell, removing all possible values of other
 *     affected cells
 *   - while there is a cell with exactly one possible value, put in this value, removing all
 *     possible values of other affected cells
 *     - check if the math of the affected cage is invalid, if this is the case: terminate with no
 *       finding
 *   - if there is no cell with a single possible:
 *     - remove all possible values of each cage if the possible is no longer valid
 */
class NishioWithPairs : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): HumanSolverStrategyResult {
        grid.cells
            .filter { it.possibles.size == 2 }
            .forEach { cell ->
                cell.possibles.forEach { possible ->
                    val nishioCore = NishioCore(grid, cell, possible)
                    val result = nishioCore.tryWithNishio()

                    if (result.hasFindings()) {
                        val changedCells = nishioCore.applyFindings(result)

                        return HumanSolverStrategyResult.Success(changedCells)
                    }
                }
            }

        return HumanSolverStrategyResult.NothingChanged()
    }
}
