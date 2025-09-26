package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.GridCage

class PossiblesReducer(
    private val cage: GridCage,
) {
    fun reduceToPossibleCombinations(possibleCombinations: Collection<IntArray>): Boolean {
        var foundPossibles = false

        cage.cells.forEachIndexed { cellIndex, cell ->
            val differentPossibles = possibleCombinations.map { it[cellIndex] }.toSet()

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
