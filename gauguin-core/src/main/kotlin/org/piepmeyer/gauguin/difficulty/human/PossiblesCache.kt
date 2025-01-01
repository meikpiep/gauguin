package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

class PossiblesCache(
    private val grid: Grid,
) {
    private val cageToPossibles = mutableMapOf<GridCage, List<IntArray>>()

    fun initialize() {
        cageToPossibles +=
            grid.cages.associateWith {
                val creator = GridSingleCageCreator(grid.variant, it)
                creator.possibleCombinations
            }
    }

    fun validateEntries() {
        cageToPossibles.forEach { (cage, possibles) ->
            val possiblesToDelete =
                possibles.filterNot {
                    cage.cells.withIndex().all { cell ->
                        if (cell.value.isUserValueSet) {
                            cell.value.userValue == it[cell.index]
                        } else {
                            cell.value.possibles.contains(it[cell.index])
                        }
                    }
                }

            if (possiblesToDelete.isNotEmpty()) {
                cageToPossibles[cage] = possibles - possiblesToDelete.toSet()
            }
        }
    }

    fun possibles(cage: GridCage): List<IntArray> = cageToPossibles[cage]!!
}
