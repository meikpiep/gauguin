package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell

internal class PossiblesCache(
    private val grid: Grid,
) {
    private var cageToPossibles = mutableMapOf<GridCage, Set<IntArray>>()

    fun initialize() {
        cageToPossibles =
            grid.cages
                .associateWith {
                    val creator = GridSingleCageCreator(grid.variant, it)
                    creator.possibleCombinations
                }.toMutableMap()
    }

    fun validateAllEntries() {
        validateEntries(grid.cells)
    }

    fun validateEntries(changedCells: List<GridCell>) {
        val changedCages = changedCells.map { it.cage() }.toSet()

        val changedCagesToPossibles =
            changedCages.associateWith { changedCage ->
                val newPossibles =
                    cageToPossibles[changedCage]!!
                        .filter {
                            changedCage.cells.withIndex().all { cell ->
                                if (cell.value.isUserValueSet) {
                                    cell.value.userValue == it[cell.index]
                                } else {
                                    cell.value.possibles.contains(it[cell.index])
                                }
                            }
                        }.toSet()

                newPossibles
            }

        cageToPossibles.putAll(changedCagesToPossibles)
    }

    fun possibles(cage: GridCage): Set<IntArray> = cageToPossibles[cage]!!
}
