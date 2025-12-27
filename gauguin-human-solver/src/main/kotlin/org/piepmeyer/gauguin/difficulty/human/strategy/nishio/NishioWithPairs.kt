package org.piepmeyer.gauguin.difficulty.human.strategy.nishio

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategyResult
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell

sealed interface NishioResult {
    class NothingFound : NishioResult

    class Contradictions : NishioResult

    class Solved(
        val solvedGrid: Grid,
    ) : NishioResult
}

private val logger = KotlinLogging.logger {}

/**
 * Uses nishio (guessing) with cells containing two or less possible numbers.
 *
 * For all cells with two possible numbers, each number is used to utilize nishio with the given
 * number. If the algorithm finds
 *   - a contradiction (meaning the conclusions from this nishio try lead to an impossible state)
 *     --> the other possbiel number gets put into the cell
 *   - all remaining cells can be filled without a contradiction
 *     --> we found the solution of the entire grid, put in the values, the grid was solved
 *
 * One possible nishio cell with one possible value is executed as follow:
 *   - put in the possible number into the nishio cell, removing all possible values of other
 *     affected cells
 *   - while there is a cell with exactly one possible value, put in this value, removing all
 *     possible values of other affected cells
 *     - check if the math of the affected cage is invalid, if this is the case: terminate with no
 *       finding
 *   - if there is no cell with a single possible:
 *     - remove all possible values of each cage if the possible is no longer valid
 */
class NishioWithPairs : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): HumanSolverStrategyResult {
        grid.cells
            .filter { it.possibles.size == 2 }
            .forEach { cell ->
                cell.possibles.forEach { possible ->
                    val result = tryWithNishio(grid, cell, possible)

                    if (result is NishioResult.Contradictions) {
                        val changedCells = grid.setUserValueAndRemovePossibles(cell, cell.possibles.first { it != possible })

                        return HumanSolverStrategyResult.Success(changedCells)
                    } else if (result is NishioResult.Solved) {
                        val cellsWithoutUserValue = grid.cells.filter { !it.isUserValueSet }
                        cellsWithoutUserValue.forEach { cell ->
                            cell.userValue = result.solvedGrid.getCell(cell.cellNumber).userValue
                        }

                        return HumanSolverStrategyResult.Success(cellsWithoutUserValue)
                    }
                }
            }

        return HumanSolverStrategyResult.NothingChanged()
    }

    fun tryWithNishio(
        grid: Grid,
        cell: GridCell,
        possible: Int,
    ): NishioResult {
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

                val deletedPossibles = tryToDeletePossibles(cageWithEmptyCells, grid)

                if (!deletedPossibles) {
                    return NishioResult.NothingFound()
                }
            } else {
                tryGrid.setUserValueAndRemovePossibles(
                    cell = cellWithSinglePossible,
                    value = cellWithSinglePossible.possibles.first(),
                )

                if (!cellWithSinglePossible.cage().isUserMathCorrect()) {
                    return NishioResult.Contradictions()
                }
            }
        } while (true)
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
}
