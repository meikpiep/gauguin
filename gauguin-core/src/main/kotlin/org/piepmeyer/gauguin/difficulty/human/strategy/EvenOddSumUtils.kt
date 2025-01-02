package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCageAction
import org.piepmeyer.gauguin.grid.GridCell

object EvenOddSumUtils {
    fun hasEvenSumsOnly(
        grid: Grid,
        cage: GridCage,
        cache: HumanSolverCache,
    ): Boolean {
        if (cage.cageType == GridCageType.SINGLE) {
            return cage.cells
                .first()
                .value
                .mod(2) == 0
        }

        if (cage.action == GridCageAction.ACTION_ADD) {
            return cage.result.mod(2) == 0
        }

        if (cage.cells.all { it.isUserValueSet }) {
            return cage.cells
                .map { it.userValue }
                .sum()
                .mod(2) == 0
        }

        return cache
            .possibles(cage)
            .map { it.sum() }
            .distinct()
            .all { it.mod(2) == 0 }
    }

    fun hasEvenSumsOnlyInCells(
        grid: Grid,
        cage: GridCage,
        cells: Set<GridCell>,
        cache: HumanSolverCache,
    ): Boolean {
        if (cage.cageType == GridCageType.SINGLE) {
            return if (cage.cells.first() in cells) {
                cage.cells
                    .first()
                    .value
                    .mod(2) == 0
            } else {
                false
            }
        }

        val filteredCells = cage.cells.filter { it in cells }

        if (filteredCells.all { it.isUserValueSet }) {
            return filteredCells.sumOf { it.userValue }.mod(2) == 0
        }

        return cache
            .possibles(cage)
            .map { it.filterIndexed { index, _ -> cage.cells[index] in cells } }
            .map { it.sum() }
            .distinct()
            .all { it.mod(2) == 0 }
    }

    fun hasOnlyEvenOrOddSums(
        grid: Grid,
        cage: GridCage,
        cache: HumanSolverCache,
    ): Boolean {
        if (cage.cageType == GridCageType.SINGLE || cage.action == GridCageAction.ACTION_ADD) {
            return true
        }

        if (cage.cells.all { it.isUserValueSet }) {
            return true
        }

        val validPossiblesSums =
            cache
                .possibles(cage)
                .map { it.sum() }
                .map { it.mod(2) == 0 }
                .distinct()

        return validPossiblesSums.size == 1
    }

    fun hasOnlyEvenOrOddSumsInCells(
        grid: Grid,
        cage: GridCage,
        cells: Set<GridCell>,
        cache: HumanSolverCache,
    ): Boolean {
        if (cage.cageType == GridCageType.SINGLE && cage.cells.first() in cells) {
            return true
        }

        val validPossiblesSums =
            cache
                .possibles(cage)
                .map { it.filterIndexed { index, _ -> cage.cells[index] in cells } }
                .map { it.sum() }
                .map { it.mod(2) == 0 }
                .distinct()

        return validPossiblesSums.size == 1
    }
}
