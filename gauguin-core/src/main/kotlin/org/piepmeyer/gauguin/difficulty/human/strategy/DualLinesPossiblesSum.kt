package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLines
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.difficulty.human.ValidPossiblesCalculator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

class DualLinesPossiblesSum : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        val linePairs = GridLines(grid).adjactPairsOflinesWithEachPossibleValue()

        linePairs.forEach { linePair ->
            val cages = linePair.first.cages() + linePair.second.cages()

            var singleCageNotCoveredByLines: GridCage? = null
            var staticGridSum = 0

            cages.forEach { cage ->
                val coveredByLines =
                    cage.cells.all {
                        linePair.first.contains(it) ||
                            linePair.second.contains(it)
                    }

                val hasAtLeastOnePossibleInLines =
                    cage.cells
                        .filter {
                            linePair.first.contains(it) ||
                                linePair.second.contains(it)
                        }.any { !it.isUserValueSet }

                if (!coveredByLines && singleCageNotCoveredByLines == null) {
                    if (!hasAtLeastOnePossibleInLines) {
                        return false
                    }
                    singleCageNotCoveredByLines = cage
                } else if (StaticSumUtils.hasStaticSum(grid, cage)) {
                    staticGridSum += StaticSumUtils.staticSum(grid, cage)
                } else {
                    return false
                }
            }

            singleCageNotCoveredByLines?.let { cage ->
                val neededSumOfLines = grid.variant.possibleDigits.sum() * 2 - staticGridSum

                val indexesInLines =
                    cage.cells.mapIndexedNotNull { index, cell ->
                        if (linePair.first.contains(cell) || linePair.second.contains(cell)) {
                            index
                        } else {
                            null
                        }
                    }

                val validPossibles = ValidPossiblesCalculator(grid, cage).calculatePossibles()
                val validPossiblesWithNeededSum =
                    validPossibles.filter {
                        it
                            .filterIndexed { index, _ ->
                                indexesInLines.contains(index)
                            }.sum() == neededSumOfLines
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
}
