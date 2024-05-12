package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

class HumanSolverStrategyRemoveImpossibleCombinationInLine : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        val lines = GridLines(grid).linesWithEachPossibleValue()

        lines.forEach { line ->
            line.cages().forEach { cage ->
                val validPossibles =
                    ValidPossiblesCalculator(grid, cage).calculatePossibles()

                cage.cells
                    .filter { line.contains(it) && !it.isUserValueSet }
                    .forEach { cell ->
                        val cellIndex = cage.cells.indexOf(cell)

                        val validPossiblesOfCell = validPossibles.map { it[cellIndex] }

                        val validPossiblesSetOfCell = validPossiblesOfCell.distinct()

                        val possiblesWithSingleCombination =
                            validPossiblesSetOfCell.filter { validPossible ->
                                validPossiblesOfCell.count { it == validPossible } == 1
                            }

                        // possiblesWithSingleCombination.
                    }
            }
        }

        return false
    }

    override fun difficulty(): Int = 25
}
