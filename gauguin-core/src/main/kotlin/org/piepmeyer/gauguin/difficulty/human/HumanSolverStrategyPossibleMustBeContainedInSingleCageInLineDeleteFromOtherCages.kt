package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

class HumanSolverStrategyPossibleMustBeContainedInSingleCageInLineDeleteFromOtherCages : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        val lines = GridLines(grid).linesWithEachPossibleValue()

        lines.forEach { line ->
            line.cages().filter { it.cells.any { !it.isUserValueSet } }
                .forEach { cage ->

                    val validPossibles =
                        ValidPossiblesCalculator(grid, cage).calculatePossibles()
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
        line.cells().filter { it.cage != cage && !it.isUserValueSet }
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

    override fun difficulty(): Int = 38
}
