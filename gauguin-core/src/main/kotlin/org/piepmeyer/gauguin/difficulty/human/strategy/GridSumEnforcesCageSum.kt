package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesCache
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

/*
 * Calculates the sum of all cages having a static cage sum. If there is exactly one cage with a
 * dynamic sum, calculate the remaining sum of it and delete all possibles which do not lead to this
 * sum.
 */
class GridSumEnforcesCageSum : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: PossiblesCache,
    ): Boolean {
        var cageWithDynamicSum: GridCage? = null
        var staticGridSum = 0

        grid.cages.forEach { cage ->
            if (StaticSumUtils.hasStaticSum(grid, cage, cache)) {
                staticGridSum += StaticSumUtils.staticSum(grid, cage, cache)
            } else if (cageWithDynamicSum == null) {
                cageWithDynamicSum = cage
            } else {
                return false
            }
        }

        cageWithDynamicSum?.let { cage ->
            val neededSumOfCage = grid.variant.possibleDigits.sum() * grid.gridSize.smallestSide() - staticGridSum

            val validPossibles = cache.possibles(cage)
            val validPossiblesWithNeededSum = validPossibles.filter { it.sum() == neededSumOfCage }

            if (validPossiblesWithNeededSum.size < validPossibles.size) {
                val reducedPossibles = PossiblesReducer(cage).reduceToPossibleCombinations(validPossiblesWithNeededSum)

                if (reducedPossibles) {
                    return true
                }
            }
        }

        return false
    }
}
