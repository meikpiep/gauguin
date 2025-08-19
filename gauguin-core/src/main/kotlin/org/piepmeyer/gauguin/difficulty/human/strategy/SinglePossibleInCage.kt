package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

/**
 * Finds a cell which only contains one single possible value regarding all possible combinations of
 * its cage and sets this value as user value.
 */
class SinglePossibleInCage : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Pair<Boolean, List<GridCell>?> {
        grid.cages
            .filter { it.cells.any { cell -> !cell.isUserValueSet } }
            .forEach { cage ->
                val validPossibles = cache.possibles(cage)

                cage.cells.forEachIndexed { index, cell ->
                    if (!cell.isUserValueSet) {
                        val possibles = validPossibles.map { it[index] }

                        if (possibles.isNotEmpty() && (possibles.size == 1 || possibles.none { it != possibles.first() })) {
                            val changedCells =
                                grid.setUserValueAndRemovePossibles(
                                    cell,
                                    possibles.first(),
                                )

                            return HumanSolverStrategy.successCellsChanged(changedCells)
                        }
                    }
                }
            }

        return HumanSolverStrategy.nothingChanged()
    }
}
