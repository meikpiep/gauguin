package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.difficulty.human.ValidPossiblesCalculator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

class GridSumEnforcesCageSum : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        if (!grid.gridSize.isSquare) {
            return false
        }

        var cageWithDynamicSum: GridCage? = null
        var staticGridSum = 0

        grid.cages.forEach { cage ->
            if (StaticSumUtils.hasStaticSum(grid, cage)) {
                staticGridSum += StaticSumUtils.staticSum(grid, cage)
            } else if (cageWithDynamicSum == null) {
                cageWithDynamicSum = cage
            } else {
                return false
            }
        }

        cageWithDynamicSum?.let { cage ->
            val neededSumOfCage = grid.variant.possibleDigits.sum() * grid.gridSize.width - staticGridSum

            val validPossibles = ValidPossiblesCalculator(grid, cage).calculatePossibles()
            val validPossiblesWithNeededSum = validPossibles.filter { it.sum() == neededSumOfCage }

            if (validPossiblesWithNeededSum.size < validPossibles.size) {
                val reducedPossibles = PossiblesReducer(grid, cage).reduceToPossileCombinations(validPossiblesWithNeededSum)

                if (reducedPossibles) {
                    return true
                }
            }
        }

        return false
    }
}
