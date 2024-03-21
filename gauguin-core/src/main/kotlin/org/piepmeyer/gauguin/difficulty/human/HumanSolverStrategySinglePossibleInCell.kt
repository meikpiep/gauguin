package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

class HumanSolverStrategySinglePossibleInCell : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        grid.cells.filter { !it.isUserValueSet }
            .firstOrNull { it.possibles.size == 1 }
            ?.let {
                grid.setUserValueAndRemovePossibles(it, it.possibles.first())

                return true
            }

        return false
    }

    override fun difficulty(): Int = 2
}
