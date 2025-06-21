package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLine
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell

class PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Pair<Boolean, List<GridCell>?> {
        val lines = cache.linesWithEachPossibleValue()

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
                            return HumanSolverStrategy.successCellsChanged(listOf(it))
                        }
                    }
                }
        }

        return HumanSolverStrategy.nothingChanged()
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
                        println("In line deletion: $line, cage to ignore $cage, $possibleToBeDeleted")
                        cell.removePossible(possibleToBeDeleted)

                        return cell
                    }
                }
            }

        return null
    }
}
