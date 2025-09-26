package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.GridLines
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCageAction

object EvenOddSumUtils {
    fun hasEvenSumsOnly(
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
                .mapNotNull { it.userValue }
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
        cage: GridCage,
        lines: GridLines,
        cache: HumanSolverCache,
    ): Boolean {
        if (cage.cageType == GridCageType.SINGLE) {
            return if (cage.cells.first() in lines.cells()) {
                cage.cells
                    .first()
                    .value
                    .mod(2) == 0
            } else {
                false
            }
        }

        val filteredCells = lines.cageCellsInLines(cage)

        if (filteredCells.all { it.isUserValueSet }) {
            return filteredCells
                .mapNotNull { it.userValue }
                .sum()
                .mod(2) == 0
        }

        return lines
            .allPossiblesInLines(cage, cache)
            .map { it.sum() }
            .distinct()
            .all { it.mod(2) == 0 }
    }

    fun hasOnlyEvenOrOddSums(
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
        cage: GridCage,
        lines: GridLines,
        cache: HumanSolverCache,
    ): Boolean {
        if (cage.cageType == GridCageType.SINGLE && cage.cells.first() in lines.cells()) {
            return true
        }

        val validPossiblesSums =
            lines
                .allPossiblesInLines(cage, cache)
                .map { it.sum() }
                .map { it.mod(2) == 0 }
                .distinct()

        return validPossiblesSums.size == 1
    }
}
