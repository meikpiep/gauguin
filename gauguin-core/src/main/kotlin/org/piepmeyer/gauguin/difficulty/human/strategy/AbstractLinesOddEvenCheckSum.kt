package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLine
import org.piepmeyer.gauguin.difficulty.human.GridLines
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.difficulty.human.ValidPossiblesCalculator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

/**
 * Scans two adjacent lines to find that each part of cage contained in this lines has a static sum
 * excluding one part of cage. The sum of this part of cages is calculated all enforced by deleting
 * deviant possibles.
 */
abstract class AbstractLinesOddEvenCheckSum(
    private val numberOfLines: Int,
) : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        val linePairs = GridLines(grid).adjacentlinesWithEachPossibleValue(numberOfLines)

        linePairs.forEach { linePair ->
            val (singleCageNotCoveredByLines, remainingSumIsEven) = calculateSingleCageCoveredByLines(grid, linePair)

            singleCageNotCoveredByLines?.let { cage ->
                val validPossibles = ValidPossiblesCalculator(grid, cage).calculatePossibles()

                val indexesInLines =
                    cage.cells.mapIndexedNotNull { index, cell ->
                        if (linePair.any { line -> line.contains(cell) }) {
                            index
                        } else {
                            null
                        }
                    }

                val validPossiblesWithNeededSum =
                    validPossibles
                        .filter {
                            it
                                .filterIndexed { index, _ ->
                                    indexesInLines.contains(index)
                                }.sum()
                                .mod(2) == if (remainingSumIsEven) 0 else 1
                        }

                if (validPossiblesWithNeededSum.isNotEmpty() && validPossiblesWithNeededSum.size < validPossibles.size) {
                    val reducedPossibles = PossiblesReducer(grid, cage).reduceToPossileCombinations(validPossiblesWithNeededSum)

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
        lines: Set<GridLine>,
    ): Pair<GridCage?, Boolean> {
        val cages = lines.map { it.cages() }.flatten().toSet()
        val lineCells = lines.map { it.cells() }.flatten().toSet()

        var cageEvenAndOddSums: GridCage? = null
        var remainingSumIsEven = (grid.variant.possibleDigits.sum() * numberOfLines).mod(2) == 0

        cages.forEach { cage ->
            if (EvenOddSumUtils.hasOnlyEvenOrOddSumsInCells(grid, cage, lineCells)) {
                val even = EvenOddSumUtils.hasEvenSumsOnlyInCells(grid, cage, lineCells)

                // if (remainingSumIsEven && even)
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
