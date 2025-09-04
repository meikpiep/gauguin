package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategyResult
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

/**
 * Finds so called Y wings: Three cells, where two 'wing' cells both see the 'pivot cell'. If:
 * - All three cells have exactly two possible values each.
 * - The pivot cell shares exactly one possible value with each pivot cell.
 * - Both wing cells share a different possible value with the pivot cell.
 *
 * Then, delete possibles from the 'opposite pivot cell', that is the cell visible from both wing
 * cells, but in the opposite direction than the pivot cell. The possibles to delete are all
 * possibles which are a possible of both wing cells.
 */
class YWing : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): HumanSolverStrategyResult {
        for (x in 0..<grid.variant.width) {
            for (y in 0..<grid.variant.height) {
                val pivotCell = grid.getValidCellAt(y, x)

                if (pivotCell.possibles.size == 2) {
                    for (x2 in 0..<grid.variant.width) {
                        for (y2 in 0..<grid.variant.height) {
                            val wingCellOne = grid.getValidCellAt(y, x2)
                            val wingCellTwo = grid.getValidCellAt(y2, x)
                            val pivotOppositeCell = grid.getValidCellAt(y2, x2)

                            if (wingCellOne.possibles.size == 2 && wingCellTwo.possibles.size == 2) {
                                val detectionResult =
                                    tryToDetectYWing(
                                        pivotCell,
                                        pivotOppositeCell,
                                        wingCellOne,
                                        wingCellTwo,
                                    )

                                if (detectionResult.first) {
                                    return HumanSolverStrategyResult.Success(detectionResult.second.toList())
                                }
                            }
                        }
                    }
                }
            }
        }

        return HumanSolverStrategyResult.NothingChanged()
    }

    private fun tryToDetectYWing(
        pivotCell: GridCell,
        pivotOppositeCell: GridCell,
        wingCellOne: GridCell,
        wingCellTwo: GridCell,
    ): Pair<Boolean, Set<GridCell>> {
        val pivotPossibles = pivotCell.possibles
        val wingCellOneIntersections = pivotPossibles.intersect(wingCellOne.possibles)
        val wingCellTwoIntersections = pivotPossibles.intersect(wingCellTwo.possibles)

        if (wingCellOneIntersections.size == 1 &&
            wingCellTwoIntersections.size == 1 &&
            wingCellOneIntersections.first() != wingCellTwoIntersections.first()
        ) {
            val interWingIntersections = wingCellOne.possibles.intersect(wingCellTwo.possibles)

            if (interWingIntersections.isNotEmpty() && pivotOppositeCell.possibles.any { it in interWingIntersections }) {
                pivotOppositeCell.possibles -= interWingIntersections

                return Pair(true, setOf(pivotOppositeCell))
            }
        }

        return noXWingFound
    }

    companion object {
        val noXWingFound = Pair<Boolean, Set<GridCell>>(false, emptySet())
    }
}
