package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLine
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

abstract class AbstractLinesSingleCagePossiblesSum(
    private val numberOfLines: Int,
) : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Boolean {
        val adjacentLinesSet = cache.adjacentlinesWithEachPossibleValue(numberOfLines)

        adjacentLinesSet.forEach { adjacentLines ->
            val (singleCageNotCoveredByLines, staticGridSum) = calculateSingleCageCoveredByLines(grid, adjacentLines, cache)

            singleCageNotCoveredByLines?.let { cage ->
                val neededSumOfLines = grid.variant.possibleDigits.sum() * numberOfLines - staticGridSum

                val validPossibles = cache.possibles(cage)
                val validPossiblesWithNeededSum =
                    validPossibles.filter {
                        adjacentLines
                            .possiblesInLines(cage, it)
                            .sum() == neededSumOfLines
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
        cache: HumanSolverCache,
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

            if (!StaticSumUtils.hasStaticSumInCells(grid, cage, lineCells, cache)) {
                if (singleCageNotCoveredByLines != null && hasAtLeastOnePossibleInLines) {
                    return Pair(null, 0)
                }

                singleCageNotCoveredByLines = cage
            } else {
                staticGridSum += StaticSumUtils.staticSumInCells(grid, cage, lineCells, cache)
            }
        }

        return Pair(singleCageNotCoveredByLines, staticGridSum)
    }
}
