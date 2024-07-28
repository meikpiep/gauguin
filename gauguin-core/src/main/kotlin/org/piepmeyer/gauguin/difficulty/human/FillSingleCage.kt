package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.Grid

class FillSingleCage {
    fun fillCells(grid: Grid): Int {
        val cagesToBeFilled = grid.cages.filter { it.cageType == GridCageType.SINGLE && !it.getCell(0).isUserValueSet }

        var filledCells = 0

        cagesToBeFilled.forEach {
            grid.setUserValueAndRemovePossibles(it.getCell(0), it.result)

            filledCells++
        }

        return filledCells
    }
}
