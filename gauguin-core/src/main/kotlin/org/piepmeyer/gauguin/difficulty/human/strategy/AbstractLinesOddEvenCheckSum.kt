package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLines
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

/**
 * Scans two adjacent lines to detect if each part of cage contained in this lines has a static sum
 * excluding one part of cage. The sum of this part of cages is calculated and enforced by deleting
 * deviant possibles.
 */
abstract class AbstractLinesOddEvenCheckSum(
    private val numberOfLines: Int,
) : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Boolean {
        val lineSets = cache.adjacentlinesWithEachPossibleValue(numberOfLines)

        lineSets.forEach { lines ->
            val (singleCageNotCoveredByLines, remainingSumIsEven) = calculateSingleCageCoveredByLines(grid, lines, cache)

            singleCageNotCoveredByLines?.let { cage ->
                val validPossibles = cache.possibles(cage)

                val validPossiblesWithNeededSum =
                    validPossibles
                        .filter {
                            lines
                                .possiblesInLines(cage, it)
                                .sum()
                                .mod(2) == if (remainingSumIsEven) 0 else 1
                        }

                if (validPossiblesWithNeededSum.isNotEmpty() && validPossiblesWithNeededSum.size < validPossibles.size) {
                    val reducedPossibles = PossiblesReducer(cage).reduceToPossibleCombinations(validPossiblesWithNeededSum)

                    if (reducedPossibles) {
                        return true
                    }
                }
            }
        }

        return false
    }

    private fun calculateSingleCageCoveredByLines(
        grid: Grid,
        lines: GridLines,
        cache: HumanSolverCache,
    ): Pair<GridCage?, Boolean> {
        val cages = lines.cages()
        val lineCells = lines.cells()

        var cageEvenAndOddSums: GridCage? = null
        var remainingSumIsEven = (grid.variant.possibleDigits.sum() * numberOfLines).mod(2) == 0

        cages.forEach { cage ->
            if (EvenOddSumUtils.hasOnlyEvenOrOddSumsInCells(grid, cage, lineCells, cache)) {
                val even = EvenOddSumUtils.hasEvenSumsOnlyInCells(grid, cage, lineCells, cache)

                remainingSumIsEven = !remainingSumIsEven.xor(even)
            } else if (cageEvenAndOddSums == null) {
                cageEvenAndOddSums = cage
            } else {
                return Pair(null, true)
            }
        }

        return Pair(cageEvenAndOddSums, remainingSumIsEven)
    }
}
