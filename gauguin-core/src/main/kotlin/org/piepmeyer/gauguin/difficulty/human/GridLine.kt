package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell

data class GridLine(
    private val grid: Grid,
    private val type: GridLineType,
    private val lineNumber: Int,
) {
    fun contains(cell: GridCell): Boolean {
        return when (type) {
            GridLineType.COLUMN -> cell.column == lineNumber
            GridLineType.ROW -> cell.row == lineNumber
        }
    }

    fun cells(): List<GridCell> {
        return grid.cells.filter { contains(it) }
    }

    fun cages(): Set<GridCage> {
        return grid.cells.filter { contains(it) }.map { it.cage!! }.toSet()
    }
}
