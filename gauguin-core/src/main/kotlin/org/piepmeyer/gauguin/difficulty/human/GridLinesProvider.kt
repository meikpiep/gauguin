package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid

class GridLinesProvider(
    private val grid: Grid,
) {
    private val gridLines = mutableMapOf<Pair<GridLineType, Int>, GridLine>()

    private val allLines: Set<GridLine> by lazy {
        val lines = mutableSetOf<GridLine>()

        for (column in 0..<grid.gridSize.width) {
            lines += fetchGridLine(GridLineType.COLUMN, column)
        }

        for (row in 0..<grid.gridSize.height) {
            lines += fetchGridLine(GridLineType.ROW, row)
        }

        lines.toSet()
    }

    private val linesWithEachPossibleValue: Set<GridLine> by lazy {
        val lines = mutableSetOf<GridLine>()

        if (grid.gridSize.height == grid.gridSize.largestSide()) {
            for (column in 0..<grid.gridSize.width) {
                lines += fetchGridLine(GridLineType.COLUMN, column)
            }
        }

        if (grid.gridSize.width == grid.gridSize.largestSide()) {
            for (row in 0..<grid.gridSize.height) {
                lines += fetchGridLine(GridLineType.ROW, row)
            }
        }

        lines.toSet()
    }

    private val adjacentlines = mutableMapOf<Int, Set<GridLines>>()
    private val adjacentlinesWithEachPossibleValue = mutableMapOf<Int, Set<GridLines>>()

    private fun fetchGridLine(
        type: GridLineType,
        lineNumber: Int,
    ): GridLine =
        gridLines.computeIfAbsent(Pair(type, lineNumber)) {
            GridLine(grid, type, lineNumber)
        }

    fun allLines(): Set<GridLine> = allLines

    fun linesWithEachPossibleValue(): Set<GridLine> = linesWithEachPossibleValue

    fun adjacentlinesWithEachPossibleValue(numberOfLines: Int): Set<GridLines> =
        adjacentlinesWithEachPossibleValue.computeIfAbsent(numberOfLines) {
            val lines = mutableSetOf<Set<GridLine>>()

            if (grid.gridSize.height == grid.gridSize.largestSide()) {
                for (column in 0..<grid.gridSize.width - numberOfLines + 1) {
                    lines +=
                        (column..<column + numberOfLines).map { fetchGridLine(GridLineType.COLUMN, it) }.toSet()
                }
            }

            if (grid.gridSize.width == grid.gridSize.largestSide()) {
                for (row in 0..<grid.gridSize.height - numberOfLines + 1) {
                    lines +=
                        (row..<row + numberOfLines).map { fetchGridLine(GridLineType.ROW, it) }.toSet()
                }
            }

            lines.map { GridLines(it) }.toSet()
        }

    fun adjacentlines(numberOfLines: Int): Set<GridLines> =
        adjacentlines.computeIfAbsent(numberOfLines) {
            val lines = mutableSetOf<Set<GridLine>>()

            for (column in 0..<grid.gridSize.width - numberOfLines + 1) {
                lines +=
                    (column..<column + numberOfLines)
                        .map { fetchGridLine(GridLineType.COLUMN, it) }
                        .toSet()
            }

            for (row in 0..<grid.gridSize.height - numberOfLines + 1) {
                lines +=
                    setOf(
                        (row..<row + numberOfLines)
                            .map { fetchGridLine(GridLineType.ROW, it) }
                            .toSet(),
                    )
            }

            lines.map { GridLines(it) }.toSet()
        }
}
