package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

class HumanSolverStrategyRemoveImpossibleCombinationInLine : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        val lines = GridLines(grid).linesWithEachPossibleValue()

        lines.forEach { line ->
            line.cages().forEach { cage ->
                val validPossibles =
                    ValidPossiblesCalculator(grid, cage).calculatePossibles()

                val lineCageCells =
                    cage.cells
                        .filter { line.contains(it) && !it.isUserValueSet }

                lineCageCells.forEach { cell ->
                    val cellIndex = cage.cells.indexOf(cell)

                    val validPossiblesOfCell = validPossibles.map { it[cellIndex] }

                    val validPossiblesSetOfCell = validPossiblesOfCell.distinct()

                    val possiblesWithSingleCombination =
                        validPossiblesSetOfCell.filter { validPossible ->
                            validPossiblesOfCell.count { it == validPossible } == 1
                        }

                    possiblesWithSingleCombination.forEach { singleCombinationPossible ->
                        val lineCageCellsIndexes =
                            lineCageCells
                                .filter { it != cell }
                                .map { cage.cells.indexOf(it) }

                        val singlePossible =
                            validPossibles
                                .first { it[cellIndex] == singleCombinationPossible }
                                .filterIndexed { index, _ ->
                                    lineCageCellsIndexes.contains(index)
                                }

                        if (singlePossible.isNotEmpty()) {
                            line
                                .cells()
                                .filter { it.cage() != cage && !it.isUserValueSet }
                                .forEach { otherCell ->
                                    if (singlePossible.containsAll(otherCell.possibles)) {
                                        cell.removePossible(singleCombinationPossible)
                                        return true
                                    }
                                }
                        }
                    }
                }
            }
        }

        return false
    }

    override fun difficulty(): Int = 25
}
