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
                                if (cache.possibles(cageOne).all { possible in it } &&
                                    cache.possibles(cageTwo).all { possible in it }
                                ) {
                                    val minimumOccurrances =
                                        cache
                                            .possibles(cageOne)
                                            .minOf { it.count { it == possible } } +
                                            cache
                                                .possibles(cageTwo)
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
