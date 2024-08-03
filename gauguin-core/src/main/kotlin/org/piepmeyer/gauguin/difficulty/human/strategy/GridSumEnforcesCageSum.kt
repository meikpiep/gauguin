package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesReducer
import org.piepmeyer.gauguin.difficulty.human.ValidPossiblesCalculator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCageAction

class GridSumEnforcesCageSum : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean {
        if (!grid.gridSize.isSquare) {
            return false
        }

        // val lines = GridLines(grid).linesWithEachPossibleValue()

        var cageWithDynamicSum: GridCage? = null
        var staticGridSum = 0

        grid.cages.forEach { cage ->
            if (hasStaticSum(grid, cage)) {
                staticGridSum += staticSum(grid, cage)
            } else if (cageWithDynamicSum == null) {
                cageWithDynamicSum = cage
            } else {
                return false
            }
        }

        cageWithDynamicSum?.let { cage ->
            val neededSumOfCage = grid.variant.possibleDigits.sum() * grid.gridSize.width - staticGridSum

            val validPossibles = ValidPossiblesCalculator(grid, cage).calculatePossibles()
            val validPossiblesWithNeededSum = validPossibles.filter { it.sum() == neededSumOfCage }

            if (validPossiblesWithNeededSum.size < validPossibles.size) {
                val reducedPossibles = PossiblesReducer(grid, cage).reduceToPossileCombinations(validPossiblesWithNeededSum)

                if (reducedPossibles) {
                    return true
                }
            }
        }

        return false
    }

    private fun staticSum(
        grid: Grid,
        cage: GridCage,
    ): Int {
        if (cage.cageType == GridCageType.SINGLE) {
            return cage.cells.first().value
        }

        if (cage.cells.all { it.isUserValueSet }) {
            return cage.cells.map { it.userValue }.sum()
        }

        return ValidPossiblesCalculator(grid, cage)
            .calculatePossibles()
            .first()
            .sum()
    }

    private fun hasStaticSum(
        grid: Grid,
        cage: GridCage,
    ): Boolean {
        if (cage.cageType == GridCageType.SINGLE || cage.action == GridCageAction.ACTION_ADD) {
            return true
        }

        val validPossiblesSums =
            ValidPossiblesCalculator(grid, cage)
                .calculatePossibles()
                .map { it.sum() }
                .distinct()

        return validPossiblesSums.size == 1
    }
}
