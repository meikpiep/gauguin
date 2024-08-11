package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.ValidPossiblesCalculator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCageAction

object EvenOddSumUtils {
    fun hasEvenSumsOnly(
        grid: Grid,
        cage: GridCage,
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

        return ValidPossiblesCalculator(grid, cage)
            .calculatePossibles()
            .map { it.sum() }
            .distinct()
            .all { it.mod(2) == 0 }
    }

    fun hasOnlyEvenOrOddSums(
        grid: Grid,
        cage: GridCage,
    ): Boolean {
        if (cage.cageType == GridCageType.SINGLE || cage.action == GridCageAction.ACTION_ADD) {
            return true
        }

        if (cage.cells.all { it.isUserValueSet }) {
            return true
        }

        val validPossiblesSums =
            ValidPossiblesCalculator(grid, cage)
                .calculatePossibles()
                .map { it.sum() }
                .map { it.mod(2) == 0 }
                .distinct()

        return validPossiblesSums.size == 1
    }
}
