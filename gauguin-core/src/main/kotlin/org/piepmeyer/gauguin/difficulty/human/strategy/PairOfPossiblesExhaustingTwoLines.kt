package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

/**
 * Finds a set of two possibles which occur in two lines and counts its minimum existence in all
 * combinations.
 */
class PairOfPossiblesExhaustingTwoLines : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Pair<Boolean, List<GridCell>?> {
        val lines = cache.adjacentlines(2)

        lines.forEach { dualLines ->

            val (cagesIntersectingWithLines, possiblesInLines) =
                GridLineHelper.getIntersectingCagesAndPossibleCombinations(
                    dualLines,
                    cache,
                )

            val combinations =
                grid.variant.possibleDigits.flatMap { firstPossible ->
                    (grid.variant.possibleDigits - firstPossible).map { setOf(it, firstPossible) }
                }

            combinations.forEach { combinationOfPossibles ->
                val cageMinimumOccurences =
                    cagesIntersectingWithLines.associateWith { cage ->
                        possiblesInLines[cage]!!.minOf { combinationOfPossibles.intersect(it).size }
                    }

                val minimumOccurences = cageMinimumOccurences.values.sum()

                if (minimumOccurences > 1) {
                    cageMinimumOccurences
                        .filter { (cage, _) -> dualLines.cageContainedCompletly(cage) }
                        .forEach { (cage, minimumOccurence) ->
                            val occurencesLeft = 4 - minimumOccurences + minimumOccurence

                            val invalidPossibles =
                                possiblesInLines[cage]!!.filter {
                                    combinationOfPossibles
                                        .intersect(
                                            it,
                                        ).size > occurencesLeft
                                }

                            if (invalidPossibles.isNotEmpty()) {
                                val validPossiblesList = possiblesInLines[cage]!!.toSet() - invalidPossibles.toSet()
                                val validPossiblesIntArray = validPossiblesList.map { it.toIntArray() }

                                val reduced = PossiblesReducer(cage).reduceToPossibleCombinations(validPossiblesIntArray)

                                if (reduced) {
                                    return HumanSolverStrategy.successCellsChanged(cage.cells)
                                }
                            }
                        }
                }
            }
        }

        return HumanSolverStrategy.nothingChanged()
    }
}
