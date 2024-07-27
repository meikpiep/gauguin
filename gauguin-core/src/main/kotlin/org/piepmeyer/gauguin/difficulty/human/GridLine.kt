package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell

data class GridLine(
    private val grid: Grid,
    val type: GridLineType,
    val lineNumber: Int,
) {
    fun contains(cell: GridCell): Boolean =
        when (type) {
            GridLineType.COLUMN -> cell.column == lineNumber
            GridLineType.ROW -> cell.row == lineNumber
        }

    fun cells(): List<GridCell> = grid.cells.filter { contains(it) }

    fun cages(): Set<GridCage> =
        grid.cells
            .filter { contains(it) }
            .map { it.cage!! }
            .toSet()

    override fun toString(): String = "GridLine type=$type, lineNumber=$lineNumber"
}
