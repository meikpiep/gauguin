package org.piepmeyer.gauguin.difficulty.human2.strategy

import org.piepmeyer.gauguin.difficulty.human2.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human2.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human2.HumanSolverStrategyResult
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

/**h
 * Calculates all possible combinations per cage and deletes one possible that is not contained
 * in one of the combinations.
 */
class RemovePossibleWithoutCombination : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): HumanSolverStrategyResult {
        val changedCells = mutableListOf<GridCell>()

        grid.cages
            .filter { it.cells.any { !it.isUserValueSet } }
            .forEach { cage ->
                val possibles = cache.possibles(cage)

                cage.cells.forEachIndexed { index, cageCell ->
                    if (!cageCell.isUserValueSet) {
                        cageCell.possibles.forEach { possibleValue ->
                            if (possibles.none { it[index] == possibleValue }) {
                                cageCell.removePossible(possibleValue)

                                changedCells.add(cageCell)
                            }
                        }
                    }
                }
            }

        if (changedCells.isNotEmpty()) {
            return HumanSolverStrategyResult.Success(changedCells.toList())
        }

        return HumanSolverStrategyResult.NothingChanged()
    }
}
