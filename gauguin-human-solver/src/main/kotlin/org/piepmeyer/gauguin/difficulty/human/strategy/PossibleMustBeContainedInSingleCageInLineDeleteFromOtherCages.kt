package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLine
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategyResult
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell

/*
 * Detects if in one line, all combinations of a cage contain the same possible. This possible then
 * gets removed from all other cells in this line.
 */
class PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): HumanSolverStrategyResult {
        val lines = cache.allLines()

        lines.forEach { line ->
            line
                .cages()
                .filter { it.cells.any { !it.isUserValueSet } }
                .forEach { cage ->

                    val validPossibles =
                        cache
                            .possibles(cage)
                            .map {
                                it.filterIndexed { index, _ ->
                                    line.contains(cage.cells[index])
                                }
                            }

                    if (validPossibles.isNotEmpty()) {
                        val possibleDigitsAlwaysInLine =
                            grid.variant.possibleDigits.filter { possible ->
                                validPossibles.all { it.contains(possible) }
                            }

                        val deletedPossibleOfCell =
                            deletePossibleInSingleCage(
                                line,
                                cage,
                                possibleDigitsAlwaysInLine,
                            )

                        deletedPossibleOfCell?.let {
                            return HumanSolverStrategyResult.Success(listOf(it))
                        }
                    }
                }
        }

        return HumanSolverStrategyResult.NothingChanged()
    }

    private fun deletePossibleInSingleCage(
        line: GridLine,
        cage: GridCage,
        possiblesToBeDeleted: List<Int>,
    ): GridCell? {
        line
            .cells()
            .filter { it.cage != cage && !it.isUserValueSet }
            .forEach { cell ->
                possiblesToBeDeleted.forEach { possibleToBeDeleted ->
                    if (cell.possibles.contains(possibleToBeDeleted)) {
                        cell.removePossible(possibleToBeDeleted)

                        return cell
                    }
                }
            }

        return null
    }
}
