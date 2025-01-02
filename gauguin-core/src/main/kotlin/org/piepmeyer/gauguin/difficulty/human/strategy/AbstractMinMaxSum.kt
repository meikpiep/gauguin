package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLines
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

abstract class AbstractMinMaxSum(
    private val numberOfLines: Int,
) : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Boolean {
        val adjacentLinesSet = cache.adjacentlinesWithEachPossibleValue(numberOfLines)

        val sumOfAdjacentLines = grid.variant.possibleDigits.sum() * numberOfLines

        adjacentLinesSet.forEach { lines ->
            val lineCages = lines.flatMap { it.cages() }.toSet()

            lineCages.forEach { cage ->

                val otherCages = lineCages - cage

                val (minSum, maxSum) = minAndMaxSum(otherCages, lines, cache)

                val possibles = lines.allPossiblesInLines(cage, cache)

                val possiblesWithinSum =
                    possibles.filter {
                        it.sum() + minSum <= sumOfAdjacentLines && it.sum() + maxSum >= sumOfAdjacentLines
                    }

                if (possiblesWithinSum.size < possibles.size) {
                    val validPossibles =
                        cache.possibles(cage).filter { possibleCombination ->
                            possiblesWithinSum.any {
                                it.contentEquals(
                                    lines.possiblesInLines(cage, possibleCombination).toIntArray(),
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
        lines: GridLines,
        cache: HumanSolverCache,
    ): Pair<Int, Int> {
        var minSum = 0
        var maxSum = 0

        otherCages.forEach { otherCage ->
            val possiblesInLines = lines.allPossiblesInLines(otherCage, cache)

            minSum += requireNotNull(possiblesInLines.minByOrNull { it.sum() }).sum()
            maxSum += requireNotNull(possiblesInLines.maxByOrNull { it.sum() }).sum()
        }

        return Pair(minSum, maxSum)
    }
}
