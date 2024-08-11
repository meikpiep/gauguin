package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.difficulty.human.ValidPossiblesCalculator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

class GridSumOddEvenCheck : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        if (!grid.gridSize.isSquare) {
            return false
        }

        var cageEvenAndOddSums: GridCage? = null
        var remainingSumIsEven = (grid.variant.possibleDigits.sum() * grid.gridSize.smallestSide()).mod(2) == 0

        grid.cages.forEach { cage ->
            if (EvenOddSumUtils.hasOnlyEvenOrOddSums(grid, cage)) {
                val even = EvenOddSumUtils.hasEvenSumsOnly(grid, cage)

                // if (remainingSumIsEven && even)
                remainingSumIsEven = !remainingSumIsEven.xor(even)
            } else if (cageEvenAndOddSums == null) {
                cageEvenAndOddSums = cage
            } else {
                return false
            }
        }

        cageEvenAndOddSums?.let { cage ->
            val validPossibles = ValidPossiblesCalculator(grid, cage).calculatePossibles()
            val validPossiblesWithNeededSum = validPossibles.filter { it.sum().mod(2) == if (remainingSumIsEven) 0 else 1 }

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
