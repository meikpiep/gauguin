package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

class ValidPossiblesCalculator(
    private val grid: Grid,
    private val cage: GridCage,
) {
    fun calculatePossibles(): List<IntArray> {
        val creator = GridSingleCageCreator(grid.variant, cage)

        return creator.possibleCombinations.filter {
            cage.cells.withIndex().all { cell ->
                if (cell.value.isUserValueSet) {
                    cell.value.userValue == it[cell.index]
                } else {
                    cell.value.possibles.contains(it[cell.index])
                }
            }
        }
    }
}
