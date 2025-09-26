package org.piepmeyer.gauguin.difficulty.human

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell

data class GridLine(
    private val grid: Grid,
    val type: GridLineType,
    val lineNumber: Int,
) {
    private val cells: List<GridCell> by lazy {
        grid.cells.filter { contains(it) }
    }

    private val cages: Set<GridCage> by lazy {
        grid.cells
            .filter { contains(it) }
            .map { it.cage!! }
            .toSet()
    }

    fun contains(cell: GridCell): Boolean =
        when (type) {
            GridLineType.COLUMN -> cell.column == lineNumber
            GridLineType.ROW -> cell.row == lineNumber
        }

    fun cells(): List<GridCell> = cells

    fun cages(): Set<GridCage> = cages

    override fun toString(): String = "GridLine type=$type, lineNumber=$lineNumber"
}
