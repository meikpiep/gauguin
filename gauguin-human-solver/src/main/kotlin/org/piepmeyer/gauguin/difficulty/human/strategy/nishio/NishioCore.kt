package org.piepmeyer.gauguin.difficulty.human.strategy.nishio

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.difficulty.human.GridLinesProvider
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell

private val logger = KotlinLogging.logger {}

class NishioCore(
    private val grid: Grid,
    private val cell: GridCell,
    private val possible: Int,
) {
    fun tryWithNishio(): NishioResult {
        val tryGrid = grid.copyWithEmptyUserValues()

        grid.cells.forEach {
            val tryCell = tryGrid.getCell(it.cellNumber)

            if (it.userValue != null) {
                tryCell.userValue = it.userValue
            } else {
                tryCell.possibles = it.possibles
            }
        }

        tryGrid.setUserValueAndRemovePossibles(tryGrid.getCell(cell.cellNumber), possible)

        logger.trace { tryGrid }

        do {
            val cellWithSinglePossible = tryGrid.cells.firstOrNull { it.possibles.size == 1 }

            if (cellWithSinglePossible == null) {
                val cageWithEmptyCells = tryGrid.cages.filter { it.cells.any { cell -> !cell.isUserValueSet } }

                if (tryGrid.cells.all { it.isUserValueSet }) {
                    return NishioResult.Solved(tryGrid)
                }

                var deletedPossibles = tryToDeletePossibles(cageWithEmptyCells, grid)

                if (!deletedPossibles) {
                    deletedPossibles = tryToDetectNakedPairs(tryGrid)
                }

                if (!deletedPossibles) {
                    return NishioResult.NothingFound()
                }
            } else {
                tryGrid.setUserValueAndRemovePossibles(
                    cell = cellWithSinglePossible,
                    value = cellWithSinglePossible.possibles.first(),
                )

                if (tryGrid.cells.any { !it.isUserValueSet && it.possibles.isEmpty() } ||
                    !cellWithSinglePossible.cage().isUserMathCorrect()
                ) {
                    return NishioResult.Contradictions()
                }
            }
        } while (true)
    }

    private fun tryToDetectNakedPairs(tryGrid: Grid): Boolean {
        GridLinesProvider(tryGrid).allLines().forEach { line ->
            val cellsWithTwoPossibles = line.cells().filter { !it.isUserValueSet && it.possibles.size == 2 }

            cellsWithTwoPossibles.forEach { firstCell ->
                cellsWithTwoPossibles.forEach { secondCell ->
                    if (firstCell != secondCell && firstCell.possibles.equals(secondCell.possibles)) {
                        val cellWithPossiblesToBeDeleted =
                            line
                                .cells()
                                .filter {
                                    it != firstCell &&
                                        it != secondCell &&
                                        !it.isUserValueSet &&
                                        (
                                            it.possibles.contains(firstCell.possibles.toList()[0]) ||
                                                it.possibles.contains(firstCell.possibles.toList()[1])
                                        )
                                }

                        if (cellWithPossiblesToBeDeleted.isNotEmpty()) {
                            cellWithPossiblesToBeDeleted.forEach { otherCell ->
                                firstCell.possibles.forEach {
                                    otherCell.removePossible(it)
                                }
                            }

                            return true
                        }
                    }
                }
            }
        }

        return false
    }

    private fun tryToDeletePossibles(
        cageWithEmptyCells: List<GridCage>,
        grid: Grid,
    ): Boolean {
        var deletedPossibles = false

        cageWithEmptyCells.forEach { cage ->
            val creator = GridSingleCageCreator(grid.variant, cage)

            val combinations = creator.possibleCombinations

            val newPossibles =
                combinations
                    .filter {
                        cage.cells.withIndex().all { cell ->
                            if (cell.value.isUserValueSet) {
                                cell.value.userValue == it[cell.index]
                            } else {
                                cell.value.possibles.contains(it[cell.index])
                            }
                        }
                    }.toSet()

            cage.cells.forEachIndexed { cellIndex, cell ->
                cell.possibles.forEach { possible ->
                    if (newPossibles.none { it[cellIndex] == possible }) {
                        cell.removePossible(possible)
                        deletedPossibles = true
                    }
                }
            }
        }
        return deletedPossibles
    }

    fun applyFindings(result: NishioResult): List<GridCell> {
        if (result is NishioResult.Contradictions) {
            val changedCells = grid.setUserValueAndRemovePossibles(cell, cell.possibles.first { it != possible })

            return changedCells
        } else if (result is NishioResult.Solved) {
            val cellsWithoutUserValue = grid.cells.filter { !it.isUserValueSet }
            cellsWithoutUserValue.forEach { cell ->
                cell.userValue = result.solvedGrid.getCell(cell.cellNumber).userValue
            }

            return cellsWithoutUserValue
        }

        return emptyList()
    }
}
