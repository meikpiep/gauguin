package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell

/*
 * Calculates the sum of all cages having a static cage sum. If there is exactly one cage with a
 * dynamic sum, calculate the remaining sum of it and delete all possibles which do not lead to this
 * sum.
 */
class GridSumEnforcesCageSum : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Pair<Boolean, List<GridCell>?> {
        var cageWithDynamicSum: GridCage? = null
        var staticGridSum = 0

        grid.cages.forEach { cage ->
            if (StaticSumUtils.hasStaticSum(cage, cache)) {
                staticGridSum += StaticSumUtils.staticSum(cage, cache)
            } else if (cageWithDynamicSum == null) {
                cageWithDynamicSum = cage
            } else {
                return HumanSolverStrategy.nothingChanged()
            }
        }

        cageWithDynamicSum?.let { cage ->
            val neededSumOfCage = grid.variant.possibleDigits.sum() * grid.gridSize.smallestSide() - staticGridSum

            val validPossibles = cache.possibles(cage)
            val validPossiblesWithNeededSum = validPossibles.filter { it.sum() == neededSumOfCage }

            if (validPossiblesWithNeededSum.size < validPossibles.size) {
                val reducedPossibles = PossiblesReducer(cage).reduceToPossibleCombinations(validPossiblesWithNeededSum)

                if (reducedPossibles) {
                    return HumanSolverStrategy.successCellsChanged(cage.cells)
                }
            }
        }

        return HumanSolverStrategy.nothingChanged()
    }
}
