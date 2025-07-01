package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCageAction
import org.piepmeyer.gauguin.grid.GridCell

object StaticSumUtils {
    fun staticSum(
        cage: GridCage,
        cache: HumanSolverCache,
    ): Int {
        if (cage.cageType == GridCageType.SINGLE) {
            return cage.cells.first().value
        }

        if (cage.cells.all { it.isUserValueSet }) {
            return cage.cells
                .mapNotNull { it.userValue }
                .sum()
        }

        return cache
            .possibles(cage)
            .first()
            .sum()
    }

    fun staticSumInCells(
        cage: GridCage,
        cells: Set<GridCell>,
        cache: HumanSolverCache,
    ): Int {
        if (cage.cageType == GridCageType.SINGLE) {
            return if (cage.cells.first() in cells) {
                cage.cells.first().value
            } else {
                0
            }
        }

        val filteredCells = cage.cells.filter { it in cells }

        if (filteredCells.all { it.isUserValueSet }) {
            return filteredCells.mapNotNull { it.userValue }.sum()
        }

        return cache
            .possibles(cage)
            .map { it.filterIndexed { index, _ -> cage.cells[index] in cells } }
            .first()
            .sum()
    }

    fun hasStaticSum(
        cage: GridCage,
        cache: HumanSolverCache,
    ): Boolean {
        if (cage.cageType == GridCageType.SINGLE || cage.action == GridCageAction.ACTION_ADD) {
            return true
        }

        val validPossiblesSums =
            cache
                .possibles(cage)
                .map { it.sum() }
                .distinct()

        return validPossiblesSums.size == 1
    }

    fun hasStaticSumInCells(
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
                .distinct()

        return validPossiblesSums.size == 1
    }
}
