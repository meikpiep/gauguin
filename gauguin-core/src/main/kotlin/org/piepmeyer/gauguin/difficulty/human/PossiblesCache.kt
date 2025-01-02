package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

internal class PossiblesCache(
    private val grid: Grid,
) {
    private var cageToPossibles = mapOf<GridCage, Set<IntArray>>()

    fun initialize() {
        cageToPossibles =
            grid.cages.associateWith {
                val creator = GridSingleCageCreator(grid.variant, it)
                creator.possibleCombinations
            }
    }

    fun validateEntries() {
        cageToPossibles =
            cageToPossibles
                .map { (cage, possibles) ->
                    val newPossibles =
                        possibles
                            .filter {
                                cage.cells.withIndex().all { cell ->
                                    if (cell.value.isUserValueSet) {
                                        cell.value.userValue == it[cell.index]
                                    } else {
                                        cell.value.possibles.contains(it[cell.index])
                                    }
                                }
                            }.toSet()

                    Pair(cage, newPossibles)
                }.toMap()
    }

    fun possibles(cage: GridCage): Set<IntArray> = cageToPossibles[cage]!!
}
