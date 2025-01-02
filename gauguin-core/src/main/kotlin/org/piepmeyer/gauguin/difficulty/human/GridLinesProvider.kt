package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

class GridLinesProvider(
    private val grid: Grid,
) {
    fun allLines(): Set<GridLine> {
        val lines = mutableSetOf<GridLine>()

        for (column in 0..<grid.gridSize.width) {
            lines += GridLine(grid, GridLineType.COLUMN, column)
        }

        for (row in 0..<grid.gridSize.height) {
            lines += GridLine(grid, GridLineType.ROW, row)
        }

        return lines
    }

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

    fun adjacentlinesWithEachPossibleValue(numberOfLines: Int): Set<GridLines> {
        val lines = mutableSetOf<Set<GridLine>>()

        if (grid.gridSize.height == grid.gridSize.largestSide()) {
            for (column in 0..<grid.gridSize.width - numberOfLines + 1) {
                lines +=
                    (column..<column + numberOfLines).map { GridLine(grid, GridLineType.COLUMN, it) }.toSet()
            }
        }

        if (grid.gridSize.width == grid.gridSize.largestSide()) {
            for (row in 0..<grid.gridSize.height - numberOfLines + 1) {
                lines +=
                    setOf(
                        (row..<row + numberOfLines).map { GridLine(grid, GridLineType.ROW, it) }.toSet(),
                    )
            }
        }

        return lines.map { GridLines(it.toSet()) }.toSet()
    }

    fun adjacentlines(numberOfLines: Int): Set<Set<GridLine>> {
        val lines = mutableSetOf<Set<GridLine>>()

        for (column in 0..<grid.gridSize.width - numberOfLines + 1) {
            lines +=
                (column..<column + numberOfLines).map { GridLine(grid, GridLineType.COLUMN, it) }.toSet()
        }

        for (row in 0..<grid.gridSize.height - numberOfLines + 1) {
            lines +=
                setOf(
                    (row..<row + numberOfLines).map { GridLine(grid, GridLineType.ROW, it) }.toSet(),
                )
        }

        return lines.toSet()
    }
}
