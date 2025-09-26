package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategyResult
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

/*
 * Detects if a grid which must contain exactly n possibles, has one possible that is containing
 * in exactly n cages, with no cage containing a possible combination where the possible is included
 * more that once.
 * If so, each such cage must include this possible exactly once to fulfill the requirement to have
 * n values of this possible.
 */
class GridEachCageWithPossibleMustIncludePossibleOnce : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): HumanSolverStrategyResult {
        val occurancesOfPossibles = grid.variant.gridSize.smallestSide()

        grid.variant.possibleDigits.forEach { possible ->
            val occurancesLeft = occurancesOfPossibles - grid.cells.count { it.userValue == possible }

            val cagesContainingPossible =
                grid.cages.filter { cage ->
                    cage.cells.any { possible in it.possibles }
                }

            if (cagesContainingPossible.size == occurancesLeft) {
                val noDoublePossibles =
                    cagesContainingPossible.all { cage ->
                        cache
                            .possibles(cage)
                            .all { combinations -> combinations.count { it == possible } <= 1 }
                    }

                if (noDoublePossibles) {
                    var reducedPossibles = false
                    val reducedCells = mutableListOf<GridCell>()

                    cagesContainingPossible.forEach { cage ->
                        val reduced =
                            PossiblesReducer(cage).reduceToPossibleCombinations(
                                cache
                                    .possibles(cage)
                                    .filter { combinations -> combinations.count { it == possible } == 1 },
                            )

                        if (reduced) {
                            reducedPossibles = true
                            reducedCells += cage.cells
                        }
                    }

                    if (reducedPossibles) {
                        return HumanSolverStrategyResult.Success(reducedCells)
                    }
                }
            }
        }

        return HumanSolverStrategyResult.NothingChanged()
    }
}
