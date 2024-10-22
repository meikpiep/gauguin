package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.GridLines
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.difficulty.human.PossiblesCache
import org.piepmeyer.gauguin.grid.Grid

class SinglePossibleInLine : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: PossiblesCache,
    ): Boolean {
        GridLines(grid).linesWithEachPossibleValue().forEach { line ->
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
                            grid.setUserValueAndRemovePossibles(cell, possible)

                            return true
                        }
                    }
                }
        }

        return false
    }
}
