package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategyResult
import org.piepmeyer.gauguin.grid.Grid

/**
 * Finds a naked quartet, that is three cells in the same row or column which have to same set of
 * exactly four possible values. As these values could not occur in any other cells beside these
 * four, these values get deleted from the other cages possibles.
 */
class NakedQuartet : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): HumanSolverStrategyResult {
        cache
            .allLines()
            .map { it.cells() }
            .forEach { lineCells ->
                val relevantCells =
                    lineCells.filter { !it.isUserValueSet && it.possibles.size <= 4 }

                if (relevantCells.size >= 4) {
                    relevantCells.forEach { cellOne ->
                        (relevantCells - cellOne).forEach { cellTwo ->
                            (relevantCells - cellOne - cellTwo).forEach { cellThree ->
                                (relevantCells - cellOne - cellTwo - cellThree).forEach { cellFour ->
                                    val possibles =
                                        cellOne.possibles + cellTwo.possibles + cellThree.possibles + cellFour.possibles

                                    if (possibles.size == 4) {
                                        val otherCellsWithPossibles =
                                            (lineCells - cellOne - cellTwo - cellThree - cellFour)
                                                .filter { !it.isUserValueSet }
                                                .filter {
                                                    it.possibles.intersect(possibles).isNotEmpty()
                                                }

                                        if (otherCellsWithPossibles.isNotEmpty()) {
                                            otherCellsWithPossibles.forEach {
                                                it.possibles -= possibles
                                            }

                                            return HumanSolverStrategyResult.Success(
                                                otherCellsWithPossibles,
                                            )
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
