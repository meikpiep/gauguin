package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLines
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategyResult
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

abstract class AbstractTwoCagesTakeAllPossibles(
    private val numberOfLines: Int,
) : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): HumanSolverStrategyResult {
        cache.adjacentlines(numberOfLines).forEach { lines ->
            val containedCages = lines.cagesContainedCompletly()

            containedCages
                .forEach { cageOne ->
                    containedCages.forEach { cageTwo ->
                        if (cageOne != cageTwo) {
                            grid.variant.possibleDigits.forEach { possible ->
                                val possiblesCageOne = cache.possibles(cageOne)
                                val possiblesCageTwo = cache.possibles(cageTwo)

                                if (possiblesCageOne.all { possible in it } &&
                                    possiblesCageTwo.all { possible in it }
                                ) {
                                    val minimumOccurrances =
                                        possiblesCageOne
                                            .minOf { it.count { it == possible } } +
                                            possiblesCageTwo
                                                .minOf { it.count { it == possible } }

                                    if (minimumOccurrances == 3) {
                                        val result = reduceIfPossible(lines, possible, cageOne, cageTwo)

                                        if (result.madeChanges()) {
                                            return result
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
        }

        return HumanSolverStrategyResult.NothingChanged()
    }
}

private fun reduceIfPossible(
    lines: GridLines,
    possible: Int,
    cageOne: GridCage,
    cageTwo: GridCage,
): HumanSolverStrategyResult {
    val otherCells =
        lines
            .cells()
            .filter { possible in it.possibles } - cageOne.cells - cageTwo.cells

    if (otherCells.isNotEmpty()) {
        otherCells.forEach { it.removePossible(possible) }

        return HumanSolverStrategyResult.Success(
            otherCells,
        )
    }

    return HumanSolverStrategyResult.NothingChanged()
}
