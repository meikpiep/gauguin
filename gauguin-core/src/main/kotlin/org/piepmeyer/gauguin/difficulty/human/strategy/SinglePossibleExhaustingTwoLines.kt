package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

/**
 * Finds a single possible which occurs in two lines and:
 *  - There is a cage containing one single occurrence for sure.
 *  - There is another cage containing a possible combination containing the possible value twice.
 *  Then, delete the combination containing the possible value twice.
 */
class SinglePossibleExhaustingTwoLines : HumanSolverStrategy {
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

            grid.variant.possibleDigits.forEach { possible ->
                val cageMinimumOccurences =
                    cagesIntersectingWithLines.associateWith { cage ->
                        if (possiblesInLines[cage]!!.all { possible in it }) 1 else 0
                    }

                val minimumOccurences = cageMinimumOccurences.values.sum()

                if (minimumOccurences == 1) {
                    cageMinimumOccurences
                        .filter { (cage, _) -> dualLines.cageContainedCompletly(cage) }
                        .forEach { (cage, minimumOccurence) ->
                            val occurencesLeft = 2 - minimumOccurences + minimumOccurence

                            val invalidPossibles =
                                possiblesInLines[cage]!!.filter {
                                    it.count { possibleValue -> possibleValue == possible } > occurencesLeft
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
