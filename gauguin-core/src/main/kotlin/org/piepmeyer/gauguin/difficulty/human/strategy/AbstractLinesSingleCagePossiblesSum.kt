package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLine
import org.piepmeyer.gauguin.difficulty.human.GridLines
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.difficulty.human.ValidPossiblesCalculator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

abstract class AbstractLinesSingleCagePossiblesSum(
    private val numberOfLines: Int,
) : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        val adjacentLinesSet = GridLines(grid).adjacentlinesWithEachPossibleValue(numberOfLines)

        adjacentLinesSet.forEach { adjacentLines ->
            val (singleCageNotCoveredByLines, staticGridSum) = calculateSingleCageCoveredByLines(grid, adjacentLines)

            singleCageNotCoveredByLines?.let { cage ->
                val neededSumOfLines = grid.variant.possibleDigits.sum() * numberOfLines - staticGridSum

                val indexesInLines =
                    cage.cells.mapIndexedNotNull { index, cell ->
                        if (adjacentLines.any { line -> line.contains(cell) }) {
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
        lines: Set<GridLine>,
    ): Pair<GridCage?, Int> {
        val cages = lines.map { it.cages() }.flatten().toSet()
        val lineCells = lines.map { it.cells() }.flatten().toSet()

        var singleCageNotCoveredByLines: GridCage? = null
        var staticGridSum = 0

        cages.forEach { cage ->
            val hasAtLeastOnePossibleInLines =
                cage.cells
                    .filter {
                        lines.any { line -> line.contains(it) }
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
