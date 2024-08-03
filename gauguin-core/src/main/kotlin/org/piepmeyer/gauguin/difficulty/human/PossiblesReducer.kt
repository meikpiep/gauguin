package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

class PossiblesReducer(
    private val grid: Grid,
    private val cage: GridCage,
) {
    fun reduceToPossileCombinations(possibleCombinations: List<IntArray>): Boolean {
        val validPossibles =
            possibleCombinations.filter { possibleCombination ->
                cage.cells.withIndex().all { cell ->
                    if (cell.value.isUserValueSet) {
                        cell.value.userValue == possibleCombination[cell.index]
                    } else {
                        cell.value.possibles.contains(possibleCombination[cell.index])
                    }
                }
            }

        var foundPossibles = false

        cage.cells.forEachIndexed { cellIndex, cell ->
            val differentPossibles = validPossibles.map { it[cellIndex] }.toSet()

            for (possible in cell.possibles) {
                if (!differentPossibles.contains(possible)) {
                    cage.getCell(cellIndex).possibles -= possible

                    foundPossibles = true
                }
            }
        }

        return foundPossibles
    }
}
