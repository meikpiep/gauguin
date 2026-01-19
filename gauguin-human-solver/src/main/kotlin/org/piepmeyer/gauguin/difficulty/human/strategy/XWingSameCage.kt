package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategyResult
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

/**
 * Detects if there are two cells in one cage, so that:
 *  - both cells are located in different rows and columns
 *  - both cells have the some set of exactly two possibles
 *  - all possible combinations lead to both cells having a different o the set of two possibles
 *
 *  This constellation enables to delete these possibles from the cells visible from both cells.
 */
class XWingSameCage : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): HumanSolverStrategyResult {
        grid.cages.forEach { cage ->
            val cellsWithTwoPossibles = cage.cells.filter { it.possibles.size == 2 }

            cellsWithTwoPossibles.forEach { firstCell ->
                cellsWithTwoPossibles.forEach { secondCell ->
                    if (firstCell != secondCell &&
                        firstCell.possibles == secondCell.possibles &&
                        firstCell.column != secondCell.column &&
                        firstCell.row != secondCell.row
                    ) {
                        val indexOne = cage.cells.indexOf(firstCell)
                        val indexTwo = cage.cells.indexOf(secondCell)

                        val possibleOne = firstCell.possibles.toList()[0]
                        val possibleTwo = firstCell.possibles.toList()[1]

                        val xorPossibles =
                            cache.possibles(cage).all {
                                (it[indexOne] == possibleOne && it[indexTwo] == possibleTwo) ||
                                    (it[indexOne] == possibleTwo && it[indexTwo] == possibleOne)
                            }

                        if (xorPossibles) {
                            val changedCells = mutableListOf<GridCell>()

                            val wingCandidates =
                                listOf(
                                    grid.getValidCellAt(firstCell.row, secondCell.column),
                                    grid.getValidCellAt(secondCell.row, firstCell.column),
                                )

                            wingCandidates.forEach { wingCandidate ->
                                if (possibleOne in wingCandidate.possibles || possibleTwo in wingCandidate.possibles) {
                                    wingCandidate.removePossible(possibleOne)
                                    wingCandidate.removePossible(possibleTwo)

                                    changedCells += wingCandidate
                                }
                            }

                            if (changedCells.isNotEmpty()) {
                                return HumanSolverStrategyResult.Success(changedCells)
                            }
                        }
                    }
                }
            }
        }

        return HumanSolverStrategyResult.NothingChanged()
    }
}
