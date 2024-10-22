package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

class PossiblesCache(
    private val grid: Grid,
) {
    private val cageToPossibles = mutableMapOf<GridCage, List<IntArray>>()

    private val cageToInitialPossibles =
        grid.cages.associateWith {
            val creator = GridSingleCageCreator(grid.variant, it)
            creator.possibleCombinations
        }

    fun calculatePossibles(cage: GridCage): List<IntArray> =
        cageToPossibles.computeIfAbsent(cage) {
            cageToInitialPossibles[cage]!!.filter {
                cage.cells.withIndex().all { cell ->
                    if (cell.value.isUserValueSet) {
                        cell.value.userValue == it[cell.index]
                    } else {
                        cell.value.possibles.contains(it[cell.index])
                    }
                }
            }
        }

    fun clear() {
        cageToPossibles.clear()
    }
}
