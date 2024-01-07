package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.Grid

class HumanSolverStrategySingleCage : HumanSolverStrategy {
    override fun fillCells(grid: Grid) {
        grid.cages.filter { it.cageType == GridCageType.SINGLE }
            .forEach { it.getCell(0).userValue = it.result }
    }
}
