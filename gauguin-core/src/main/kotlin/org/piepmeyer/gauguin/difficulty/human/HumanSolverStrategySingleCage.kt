package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.Grid

class HumanSolverStrategySingleCage : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        val cagesToBeFilled = grid.cages.filter { it.cageType == GridCageType.SINGLE && !it.getCell(0).isUserValueSet }

        cagesToBeFilled.firstOrNull()?.let {
            grid.setUserValueAndRemovePossibles(it.getCell(0), it.result)

            return true
        }

        return false
    }

    override fun difficulty(): Int = 1
}
