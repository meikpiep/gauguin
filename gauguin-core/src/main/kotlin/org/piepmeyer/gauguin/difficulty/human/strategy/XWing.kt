package org.piepmeyer.gauguin.difficulty.human.strategy

import org.piepmeyer.gauguin.difficulty.human.HumanSolverCache
import org.piepmeyer.gauguin.difficulty.human.HumanSolverStrategy
import org.piepmeyer.gauguin.grid.Grid

class XWing : HumanSolverStrategy {
    override fun fillCells(
        grid: Grid,
        cache: HumanSolverCache,
    ): Boolean {
        for (x in 0..<grid.variant.width) {
            for (y in 0..<grid.variant.height) {
                if (grid.getValidCellAt(y, x).possibles.size == 2) {
                    for (x2 in x + 1..<grid.variant.width) {
                        for (y2 in y + 1..<grid.variant.height) {
                            val topLeft = grid.getValidCellAt(y, x).possibles
                            val topRight = grid.getValidCellAt(y, x2).possibles
                            val bottomLeft = grid.getValidCellAt(y2, x).possibles
                            val bottomRight = grid.getValidCellAt(y2, x2).possibles

                            if (topRight.size == 2 &&
                                bottomLeft.size == 2 &&
                                bottomRight.size == 2
                            ) {
                                val commonPossibles = topLeft.intersect(bottomRight)

                                if (commonPossibles.isNotEmpty()) {
                                    if ((topRight == bottomRight && bottomLeft == topLeft) ||
                                        (topRight == topLeft && bottomLeft == bottomRight)
                                    ) {
                                        val adjacentCells =
                                            grid.getCellsAtSameRow(grid.getValidCellAt(y, x)) +
                                                grid.getCellsAtSameColumn(
                                                    grid.getValidCellAt(
                                                        y,
                                                        x,
                                                    ),
                                                ) +
                                                grid.getCellsAtSameRow(
                                                    grid.getValidCellAt(
                                                        y2,
                                                        x2,
                                                    ),
                                                ) +
                                                grid.getCellsAtSameColumn(
                                                    grid.getValidCellAt(
                                                        y2,
                                                        x2,
                                                    ),
                                                )

                                        val adjacentCellsSet =
                                            adjacentCells.toSet() -
                                                grid.getValidCellAt(y, x) -
                                                grid.getValidCellAt(y, x2) -
                                                grid.getValidCellAt(y2, x) -
                                                grid.getValidCellAt(y2, x2)

                                        if (adjacentCellsSet.any {
                                                it.possibles
                                                    .intersect(
                                                        commonPossibles,
                                                    ).isNotEmpty()
                                            }
                                        ) {
                                            adjacentCellsSet.forEach {
                                                it.possibles -= commonPossibles
                                            }

                                            return true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return false
    }
}
