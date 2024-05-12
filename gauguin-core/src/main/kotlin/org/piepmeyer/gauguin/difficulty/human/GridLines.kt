package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

class GridLines(
    private val grid: Grid,
) {
    fun linesWithEachPossibleValue(): Set<GridLine> {
        val lines = mutableSetOf<GridLine>()

        if (grid.gridSize.height == grid.gridSize.largestSide()) {
            for (column in 0..<grid.gridSize.width) {
                lines += GridLine(grid, GridLineType.COLUMN, column)
            }
        }

        if (grid.gridSize.width == grid.gridSize.largestSide()) {
            for (row in 0..<grid.gridSize.height) {
                lines += GridLine(grid, GridLineType.ROW, row)
            }
        }

        return lines
    }
}
