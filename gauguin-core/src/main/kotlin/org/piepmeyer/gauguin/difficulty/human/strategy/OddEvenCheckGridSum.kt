package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell

/*
 * Calculates the even/odd sum of all cages having a static cage sum. If there is exactly one cage
 * with a dynamic even/odd sum, the even/odd state of the remaining cage gets calculated. All
 * combinations which do not lead to such a even or odd sum get deleted.
 */
class OddEvenCheckGridSum : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Pair<Boolean, List<GridCell>?> {
        var cageEvenAndOddSums: GridCage? = null
        var remainingSumIsEven = (grid.variant.possibleDigits.sum() * grid.gridSize.smallestSide()).mod(2) == 0

        grid.cages.forEach { cage ->
            if (EvenOddSumUtils.hasOnlyEvenOrOddSums(cage, cache)) {
                val even = EvenOddSumUtils.hasEvenSumsOnly(cage, cache)

                remainingSumIsEven = !remainingSumIsEven.xor(even)
            } else if (cageEvenAndOddSums == null) {
                cageEvenAndOddSums = cage
            } else {
                return HumanSolverStrategy.nothingChanged()
            }
        }

        cageEvenAndOddSums?.let { cage ->
            val validPossibles = cache.possibles(cage)
            val validPossiblesWithNeededSum = validPossibles.filter { it.sum().mod(2) == if (remainingSumIsEven) 0 else 1 }

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
