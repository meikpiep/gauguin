package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLines
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

abstract class AbstractTwoCellsPossiblesSum(
    private val numberOfLines: Int,
) : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Boolean {
        val adjacentLinesSet = cache.adjacentlinesWithEachPossibleValue(numberOfLines)

        adjacentLinesSet.forEach { adjacentLines ->
            val (cellsNotCoveredByLines, staticGridSum) = calculateTwoCellsCoveredByLines(adjacentLines, cache)

            if (cellsNotCoveredByLines.size == 2) {
                val neededSumOfLines = grid.variant.possibleDigits.sum() * numberOfLines - staticGridSum

                var found = false

                cellsNotCoveredByLines.forEach { cell ->
                    val otherCell = (cellsNotCoveredByLines - cell).first()

                    cell.possibles.forEach { possible ->
                        if (!otherCell.possibles.contains(neededSumOfLines - possible)) {
                            found = true
                            cell.possibles -= possible
                        }
                    }
                }

                if (found) {
                    return true
                }
            }
        }

        return false
    }

    private fun calculateTwoCellsCoveredByLines(
        lines: GridLines,
        cache: HumanSolverCache,
    ): Pair<List<GridCell>, Int> {
        val cages = lines.cages()
        val lineCells = lines.cells()

        val cellsNotCoveredByLines = mutableListOf<GridCell>()
        var staticGridSum = 0

        cages.forEach { cage ->
            val dynamicSumCells =
                cage.cells
                    .filter {
                        lines.any { line -> line.contains(it) }
                    }.filter { !it.isUserValueSet }

            if (!StaticSumUtils.hasStaticSumInCells(cage, lineCells, cache)) {
                cellsNotCoveredByLines += dynamicSumCells
                staticGridSum +=
                    cage.cells
                        .filter {
                            lines.any { line -> line.contains(it) }
                        }.filter { it.isUserValueSet }
                        .map { it.userValue }
                        .sum()

                if (cellsNotCoveredByLines.size > 2) {
                    return Pair(emptyList(), 0)
                }
            } else {
                staticGridSum += StaticSumUtils.staticSumInCells(cage, lineCells, cache)
            }
        }

        return Pair(cellsNotCoveredByLines, staticGridSum)
    }
}
