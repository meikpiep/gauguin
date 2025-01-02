package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLinesProvider
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

class PossibleMustBeContainedInSingleCageInLine : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Boolean {
        val lines = GridLinesProvider(grid).linesWithEachPossibleValue()

        lines.forEach { line ->
            for (singlePossible in grid.variant.possibleDigits) {
                val cagesWithPossible =
                    line
                        .cells()
                        .filter { it.possibles.contains(singlePossible) }
                        .map { it.cage!! }
                        .toSet()

                if (cagesWithPossible.size == 1) {
                    val cage = cagesWithPossible.first()

                    val validPossibles =
                        cache
                            .possibles(cage)
                            .filter {
                                it.withIndex().any { possibleWithIndex ->
                                    possibleWithIndex.value == singlePossible &&
                                        line.contains(cage.cells[possibleWithIndex.index])
                                }
                            }

                    if (validPossibles.isNotEmpty()) {
                        if (deletePossibleInSingleCage(cage, validPossibles)) return true
                    }
                }
            }
        }

        return false
    }

    private fun deletePossibleInSingleCage(
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
