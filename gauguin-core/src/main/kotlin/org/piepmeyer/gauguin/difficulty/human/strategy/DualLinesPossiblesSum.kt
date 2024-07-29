package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid

class DualLinesPossiblesSum : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        /*val linePairs = GridLines(grid).adjactPairsOflinesWithEachPossibleValue()

        linePairs.forEach { linePair ->
            linePair
                .cages()
                .filter { it.cells.any { !it.isUserValueSet } }
                .forEach { cage ->

                    val validPossibles =
                        ValidPossiblesCalculator(grid, cage)
                            .calculatePossibles()
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
        }*/

        return false
    }
}
