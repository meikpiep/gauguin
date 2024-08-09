package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLine
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
            val (singleCageNotCoveredByLines, staticGridSum) = calculateSingleCageCoveredByLines(grid, linePair)

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

    private fun calculateSingleCageCoveredByLines(
        grid: Grid,
        linePair: Pair<GridLine, GridLine>,
    ): Pair<GridCage?, Int> {
        val cages = linePair.first.cages() + linePair.second.cages()
        val lineCells = linePair.first.cells() + linePair.second.cells()

        var singleCageNotCoveredByLines: GridCage? = null
        var staticGridSum = 0

        cages.forEach { cage ->
            val hasAtLeastOnePossibleInLines =
                cage.cells
                    .filter {
                        linePair.first.contains(it) ||
                            linePair.second.contains(it)
                    }.any { !it.isUserValueSet }

            if (!StaticSumUtils.hasStaticSumInCells(grid, cage, lineCells)) {
                if (singleCageNotCoveredByLines != null && hasAtLeastOnePossibleInLines) {
                    return Pair(null, 0)
                }

                singleCageNotCoveredByLines = cage
            } else {
                staticGridSum += StaticSumUtils.staticSumInCells(grid, cage, lineCells)
            }
        }

        return Pair(singleCageNotCoveredByLines, staticGridSum)
    }
}
