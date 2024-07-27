package org.piepmeyer.gauguin.difficulty.human

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

private val logger = KotlinLogging.logger {}

/*
 * Detects and deletes possibles if a possible is included in a single combination
 * of the cage and that combination may not be chosen because there is another cell
 * in the line which only has possibles left contained in the single combination
 */
class HumanSolverStrategyRemoveImpossibleCombinationInLineBecauseOfSingleCell : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean = ImpossibleCombinationInLineDetector.fillCells(grid, this::isImpossible)

    private fun isImpossible(
        grid: Grid,
        line: GridLine,
        cage: GridCage,
        singlePossible: List<Int>,
    ): Boolean {
        line
            .cells()
            .filter { it.cage() != cage && !it.isUserValueSet }
            .forEach { otherCell ->
                if (singlePossible.containsAll(otherCell.possibles)) {
                    return true
                }
            }

        return false
    }

    override fun difficulty(): Int = 25
}
