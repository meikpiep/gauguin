package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLine
import org.piepmeyer.gauguin.difficulty.human.GridLines
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesCache
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

abstract class AbstractMinMaxSum(
    private val numberOfLines: Int,
) : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: PossiblesCache,
    ): Boolean {
        val adjacentLinesSet = GridLines(grid).adjacentlinesWithEachPossibleValue(numberOfLines)

        val sumOfAdjacentLines = grid.variant.possibleDigits.sum() * numberOfLines

        adjacentLinesSet.forEach { lines ->
            val lineCages = lines.flatMap { it.cages() }.toSet()

            lineCages.forEach { cage ->

                val otherCages = lineCages - cage

                val (minSum, maxSum) = minAndMaxSum(otherCages, lines, cache)

                val possibles = possiblesInLines(cage, lines, cache)

                val possiblesWithinSum =
                    possibles.filter {
                        it.sum() + minSum <= sumOfAdjacentLines && it.sum() + maxSum >= sumOfAdjacentLines
                    }

                if (possiblesWithinSum.size < possibles.size) {
                    val cellIndexes = cellIndexesInLine(cage, lines)

                    val validPossibles =
                        cache.possibles(cage).filter { possibles ->
                            possiblesWithinSum.any {
                                it.contentEquals(
                                    possibles.filterIndexed { index, _ -> cellIndexes.contains(index) }.toIntArray(),
                                )
                            }
                        }

                    val reduced = PossiblesReducer(cage).reduceToPossibleCombinations(validPossibles)

                    if (reduced) {
                        return true
                    }
                }
            }
        }

        return false
    }

    private fun minAndMaxSum(
        otherCages: Set<GridCage>,
        lines: Set<GridLine>,
        cache: PossiblesCache,
    ): Pair<Int, Int> {
        var minSum = 0
        var maxSum = 0

        otherCages.forEach { otherCage ->
            val possiblesInLines = possiblesInLines(otherCage, lines, cache)

            minSum += requireNotNull(possiblesInLines.minByOrNull { it.sum() }).sum()
            maxSum += requireNotNull(possiblesInLines.maxByOrNull { it.sum() }).sum()
        }

        return Pair(minSum, maxSum)
    }

    private fun possiblesInLines(
        cage: GridCage,
        lines: Set<GridLine>,
        cache: PossiblesCache,
    ): List<IntArray> {
        val cellIndexesInLine =
            cellIndexesInLine(cage, lines)

        val possiblesInLines =
            cache.possibles(cage).map {
                it
                    .filterIndexed { index, _ ->
                        cellIndexesInLine.contains(index)
                    }.toIntArray()
            }

        return possiblesInLines
    }

    private fun cellIndexesInLine(
        cage: GridCage,
        lines: Set<GridLine>,
    ): List<Int> {
        val cellIndexesInLine =
            cage.cells
                .mapIndexed { index, cell ->
                    if (lines.any { line -> line.contains(cell) }) {
                        index
                    } else {
                        null
                    }
                }.filterNotNull()
        return cellIndexesInLine
    }
}
