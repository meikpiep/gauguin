package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

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
                        if (otherCellsInLine
                                .map { it.possibles }
                                .none { it.contains(possible) }
                        ) {
                            val changedCells = grid.setUserValueAndRemovePossibles(cell, possible)

                            return HumanSolverStrategy.successCellsChanged(changedCells)
                        }
                    }
                }
        }

        return HumanSolverStrategy.nothingChanged()
    }
}
