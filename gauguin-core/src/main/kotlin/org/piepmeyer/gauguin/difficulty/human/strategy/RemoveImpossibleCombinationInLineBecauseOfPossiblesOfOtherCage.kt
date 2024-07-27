package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLine
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.ValidPossiblesCalculator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage

/*
 * Detects and deletes possibles if a possible is included in a single combination
 * of the cage and that combination may not be chosen because there is another cell
 * in the line which only has possibles left contained in the single combination
 */
class RemoveImpossibleCombinationInLineBecauseOfPossiblesOfOtherCage : HumanSolverStrategy {
    override fun fillCells(grid: Grid): Boolean = ImpossibleCombinationInLineDetector.fillCells(grid, this::isImpossible)

    private fun isImpossible(
        grid: Grid,
        line: GridLine,
        cage: GridCage,
        singlePossible: List<Int>,
    ): Boolean {
        line
            .cages()
            .filter { it != cage }
            .forEach { otherCage ->
                val validPossiblesOtherCage =
                    ValidPossiblesCalculator(grid, otherCage).calculatePossibles()

                val otherCageLineCellsIndexes =
                    otherCage.cells
                        .filter { line.contains(it) && !it.isUserValueSet }
                        .map { otherCage.cells.indexOf(it) }

                val allPossiblesInvalid =
                    validPossiblesOtherCage.all { validPossibles ->
                        validPossibles
                            .filterIndexed { index, _ ->
                                otherCageLineCellsIndexes.contains(index)
                            }.intersect(singlePossible.toSet())
                            .isNotEmpty()
                    }

                if (allPossiblesInvalid) {
                    return true
                }
            }

        return false
    }

    override fun difficulty(): Int = 25
}
