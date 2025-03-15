package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

/**
 * Finds a naked pair, that is two cells in the same row or column which have to same list of
 * exactly two possible values. As these values could not occur in any other cells beside these
 * two, these values get deleted from the other cages possibles.
 */
class NakedPairWithCage : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Boolean {
        val cellsWithoutUserValue = grid.cells

        cache.allLines().forEach { line ->

            line
                .cells()
                .filter { !it.isUserValueSet && it.possibles.size == 2 }
                .forEach { cell ->
                    val potentialNakedPair = cell.possibles

                    line
                        .cages()
                        .filter { it != cell.cage() }
                        .forEach { otherCage ->
                            cache.possibles(otherCage).all { possibleCombination ->
                                var found = false

                                possibleCombination.forEachIndexed { index, i ->
                                    if (line.contains(otherCage.cells[index]) && i in potentialNakedPair) {
                                        if (!found) {
                                            found = true
                                        } else {
                                            val cellsWithPossibles =
                                                line.cells() - cell -
                                                    otherCage.cells
                                                        .filter { !it.isUserValueSet }
                                                        .filter { it.possibles.intersect(potentialNakedPair).isNotEmpty() }

                                            if (cellsWithPossibles.isNotEmpty()) {
                                                cellsWithPossibles.forEach {
                                                    it.possibles -= potentialNakedPair
                                                }

                                                return@forEach true
                                            }
                                        }
                                    }
                                }

                                return@forEach false
                            }
                        }
                }
        }

        return false
    }

    private fun isNakedPair(
        cell: GridCell,
        otherCell: GridCell,
    ) = cell != otherCell &&
        (cell.row == otherCell.row || cell.column == otherCell.column) &&
        cell.possibles.size == 2 &&
        otherCell.possibles.size == 2 &&
        cell.possibles.containsAll(otherCell.possibles)
}
