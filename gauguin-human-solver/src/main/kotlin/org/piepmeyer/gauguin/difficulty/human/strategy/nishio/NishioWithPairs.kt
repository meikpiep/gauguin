package org.piepmeyer.gauguin.difficulty.human.strategy.nishio

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategyResult
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

enum class NishioResult {
    NOTHING_FOUND,
    CONTRADICTION,
    SOLVED,
}

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

                    if (result == NishioResult.CONTRADICTION) {
                        val changedCells = grid.setUserValueAndRemovePossibles(cell, cell.possibles.first { it != possible })

                        return HumanSolverStrategyResult.Success(changedCells)
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

        // tryGrid.cells.any { !it.isUserValueSet && it.possibles.size == 0 }

        println(tryGrid)

        do {
            val cellWithSinglePossible = tryGrid.cells.firstOrNull { it.possibles.size == 1 }

            if (cellWithSinglePossible == null) {
                val cageWithEmptyCells = tryGrid.cages.filter { it.cells.any { !it.isUserValueSet } }

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

                if (!deletedPossibles) {
                    return NishioResult.NOTHING_FOUND
                }
            } else {
                tryGrid.setUserValueAndRemovePossibles(
                    cell = cellWithSinglePossible,
                    value = cellWithSinglePossible.possibles.first(),
                )

                if (!cellWithSinglePossible.cage().isUserMathCorrect()) {
                    return NishioResult.CONTRADICTION
                }
            }
        } while (true)
    }
}
