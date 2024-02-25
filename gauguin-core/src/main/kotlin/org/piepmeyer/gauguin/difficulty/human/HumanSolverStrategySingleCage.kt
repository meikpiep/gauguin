package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.Grid

class HumanSolverStrategySingleCage : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        val cagesToBeFilled = grid.cages.filter { it.cageType == GridCageType.SINGLE && !it.getCell(0).isUserValueSet }

        cagesToBeFilled.forEach {
            grid.setUserValueAndRemovePossibles(it.getCell(0), it.result)
        }

        return cagesToBeFilled.isNotEmpty()
    }
}
