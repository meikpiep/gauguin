package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

/**
 * Finds if two lines exist with
 *   - one cage containing a possible exactly once in each possible combination
 *   - a second cage containing combinations with the possible
 *   - no other cell of both lines contains this possible.
 * Ensures that the second cage holds only possibles containing to possible exactly once by deleting
 * other combinations from it.
 */
class EnforceLastOccuranceInDualLines : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Pair<Boolean, List<GridCell>?> {
/*        cache.adjacentlinesWithEachPossibleValue(2).forEach { dualLines ->
            dualLines
                .cells()
                .filter { !it.isUserValueSet }
                .filter { it.possibles.size == 2 }
                .forEach { cell ->

                    line.cages().forEach { cage ->

                        if (cage != cell.cage()) {
                            val containsOnePairValue =
                                line
                                    .allPossiblesInLines(cage, cache)
                                    .all {
                                        it.contains(cell.possibles.elementAt(0)) ||
                                            it.contains(cell.possibles.elementAt(1))
                                    }

                            if (containsOnePairValue) {
                                val cellsToReduce =
                                    line
                                        .cells()
                                        .filter { it != cell }
                                        .filter { it.cage() != cage }
                                        .filter {
                                            it.possibles.contains(cell.possibles.elementAt(0)) ||
                                                it.possibles.contains(cell.possibles.elementAt(1))
                                        }

                                if (cellsToReduce.isNotEmpty()) {
                                    cellsToReduce.forEach {
                                        it.possibles -= cell.possibles
                                    }

                                    return HumanSolverStrategy.successCellsChanged(cellsToReduce)
                                }
                            }
                        }
                    }
                }
        }*/

        return HumanSolverStrategy.nothingChanged()
    }
}
