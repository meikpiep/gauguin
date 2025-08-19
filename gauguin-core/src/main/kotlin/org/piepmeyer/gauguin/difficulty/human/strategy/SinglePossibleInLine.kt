package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

/**
 * If a single line with all possibles contains a possible number only in one cage, then enforce
 * this cage to only contain combinations including this possible.
 */
class SinglePossibleInLine : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Pair<Boolean, List<GridCell>?> {
        cache.linesWithEachPossibleValue().forEach { line ->
            line
                .cells()
                .filter { !it.isUserValueSet }
                .forEach { cell ->
                    val otherCellsInLine = line.cells() - cell

                    cell.possibles.forEach { possible ->
                        if (otherCellsInLine.none { it.possibles.contains(possible) }) {
                            val changedCells = grid.setUserValueAndRemovePossibles(cell, possible)

                            return HumanSolverStrategy.successCellsChanged(changedCells)
                        }
                    }
                }
        }

        return HumanSolverStrategy.nothingChanged()
    }
}
