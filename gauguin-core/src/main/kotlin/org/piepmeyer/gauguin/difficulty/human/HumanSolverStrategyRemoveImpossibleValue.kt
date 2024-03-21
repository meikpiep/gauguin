package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.grid.Grid

class HumanSolverStrategyRemoveImpossibleValue : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        grid.cages
            .filter { it.cells.any { !it.isUserValueSet } }
            .forEach { cage ->

                val creator = GridSingleCageCreator(grid.variant, cage)

                val possibles = creator.possibleCombinations

                val cageCellsWithoutUserValue = cage.cells.filter { !it.isUserValueSet }

                for (cageCellWithIndex in cage.cells.withIndex()) {
                    val index = cageCellWithIndex.index
                    val cageCell = cageCellWithIndex.value

                    if (cageCellsWithoutUserValue.contains(cageCell)) {
                        cageCell.possibles.forEach { possibleValue ->
                            if (possibles.none { it[index] == possibleValue }) {
                                cageCell.removePossible(possibleValue)

                                return true
                            }
                        }
                    }
                }
            }

        return false
    }

    override fun difficulty(): Int = 10
}
