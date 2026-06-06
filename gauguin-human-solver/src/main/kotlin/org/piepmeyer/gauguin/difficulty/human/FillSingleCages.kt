package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCageAction

class FillSingleCages {
    fun fillCells(grid: Grid): Int {
        val cagesToBeFilled =
            grid.cages.filter {
                it.cageType == GridCageType.SINGLE &&
                    it.action == GridCageAction.ACTION_NONE &&
                    !it.getCell(0).isUserValueSet
            }

        var filledCells = 0

        cagesToBeFilled.forEach {
            grid.setUserValueAndRemovePossibles(it.getCell(0), it.result)

            filledCells++
        }

        return filledCells
    }
}
