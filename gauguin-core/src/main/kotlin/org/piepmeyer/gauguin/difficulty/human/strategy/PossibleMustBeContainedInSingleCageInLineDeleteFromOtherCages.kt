package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLine
import org.piepmeyer.gauguin.difficulty.human.GridLines
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesCache
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

class PossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: PossiblesCache,
    ): Boolean {
        val lines = GridLines(grid).linesWithEachPossibleValue()

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

                        if (deletePossibleInSingleCage(
                                line,
                                cage,
                                possibleDigitsAlwaysInLine,
                            )
                        ) {
                            return true
                        }
                    }
                }
        }

        return false
    }

    private fun deletePossibleInSingleCage(
        line: GridLine,
        cage: GridCage,
        possiblesToBeDeleted: List<Int>,
    ): Boolean {
        line
            .cells()
            .filter { it.cage != cage && !it.isUserValueSet }
            .forEach { cell ->
                possiblesToBeDeleted.forEach { possibleToBeDeleted ->
                    if (cell.possibles.contains(possibleToBeDeleted)) {
                        println("In line deletion: $line, cage to ignore $cage, $possibleToBeDeleted")
                        cell.removePossible(possibleToBeDeleted)

                        return true
                    }
                }
            }

        return false
    }
}
