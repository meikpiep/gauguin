package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.grid.Grid

class HumanSolverStrategySinglePossible : HumanSolverStrategy {
    override fun fillCells(grid: Grid) {
        grid.cages.filter { it.cells.any { !it.isUserValueSet } }
            .forEach {
                val creator = GridSingleCageCreator(grid.variant, it)

                creator.possibleNums
            }
    }
}
