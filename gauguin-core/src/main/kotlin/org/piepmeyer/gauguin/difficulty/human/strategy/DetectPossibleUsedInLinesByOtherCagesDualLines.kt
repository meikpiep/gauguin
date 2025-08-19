package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

/**
 * Detects if there are two cages on two adjacent lines where
 *  - Two cages contain one or more possible values in each of their combinations.
 *  - The two cages share one or more such possible values.
 * Then, all shared possibles values get eliminated from other cages.
 */
class DetectPossibleUsedInLinesByOtherCagesDualLines : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Pair<Boolean, List<GridCell>?> {
        val lines = cache.adjacentlines(2)

        lines.forEach { dualLines ->

            val cellsOfLines = dualLines.cells()

            val (cagesIntersectingWithLines, possiblesInLines) = GridLineHelper.getIntersectingCagesAndPossibles(dualLines, cache)

            cagesIntersectingWithLines.forEach { firstCage ->
                val possiblesForFirstCage = possiblesInLines[firstCage]!!

                val possibleInEachFirstCageCombination =
                    grid.variant.possibleDigits.filter { possible ->
                        possiblesForFirstCage.all { it.contains(possible) }
                    }

                if (possibleInEachFirstCageCombination.isNotEmpty()) {
                    cagesIntersectingWithLines
                        .filter { it.id > firstCage.id }
                        .forEach { otherCage ->
                            val possiblesForOtherCage = possiblesInLines[otherCage]!!

                            val possibleInEachOtherCageCombination =
                                grid.variant.possibleDigits.filter { possible ->
                                    possiblesForOtherCage.all { it.contains(possible) }
                                }

                            val possiblesInBothCages =
                                possibleInEachFirstCageCombination.intersect(
                                    possibleInEachOtherCageCombination.toSet(),
                                )

                            if (possiblesInBothCages.isNotEmpty()) {
                                val foreignCells = cellsOfLines - firstCage.cells.toSet() - otherCage.cells.toSet()

                                var found = false

                                possiblesInBothCages.forEach { possible ->
                                    if (foreignCells.any { it.possibles.contains(possible) }) {
                                        found = true

                                        foreignCells.forEach { it.possibles -= possible }
                                    }
                                }

                                if (found) {
                                    return HumanSolverStrategy.successCellsChanged(foreignCells.toList())
                                }
                            }
                        }
                }
            }
        }

        return HumanSolverStrategy.nothingChanged()
    }
}
