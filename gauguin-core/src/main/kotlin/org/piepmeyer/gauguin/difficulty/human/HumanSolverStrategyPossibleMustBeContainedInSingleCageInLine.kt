package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

class HumanSolverStrategyPossibleMustBeContainedInSingleCageInLine : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        val lines = GridLines(grid).linesWithEachPossibleValue()

        lines.forEach { line ->
            for (singlePossible in grid.variant.possibleDigits) {
                val cagesWithPossible =
                    line.cells()
                        .filter { it.possibles.contains(singlePossible) }
                        .map { it.cage!! }
                        .toSet()

                if (cagesWithPossible.size == 1) {
                    val cage = cagesWithPossible.first()

                    val validPossibles =
                        validPossibles(grid, cage)
                            .filter {
                                it.withIndex().any { possibleWithIndex ->
                                    possibleWithIndex.value == singlePossible &&
                                        line.contains(cage.cells[possibleWithIndex.index])
                                }
                            }

                    if (deletePossible(cage, validPossibles)) return true
                }
            }
        }

        return false
    }

    private fun validPossibles(
        grid: Grid,
        cage: GridCage,
    ): List<IntArray> {
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

    private fun deletePossible(
        cage: GridCage,
        validPossibles: List<IntArray>,
    ): Boolean {
        for (cellNumber in 0..<cage.cells.size) {
            val differentPossibles = validPossibles.map { it[cellNumber] }.toSet()

            for (possible in cage.getCell(cellNumber).possibles) {
                if (!differentPossibles.contains(possible)) {
                    cage.getCell(cellNumber).possibles -= possible

                    return true
                }
            }
        }

        return false
    }
}
