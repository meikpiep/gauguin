package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategyResult
import org.piepmeyer.gauguin.grid.Grid

/**
 * Finds a hidden pair, in this case both
 *   - a single cell containing exactly two possibles [a, b]
 *   - a cage which does not contain the single cell where each combination either contains a or b.
 * in the same line. If this holds, the strategy eliminates [a, b] from any other cells of the line.
 */
class HiddenPair : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): HumanSolverStrategyResult {
        cache.adjacentlines(1).forEach { line ->
            line
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

                                    return HumanSolverStrategyResult.Success(cellsToReduce)
                                }
                            }
                        }
                    }
                }
        }

        return HumanSolverStrategyResult.NothingChanged()
    }
}
