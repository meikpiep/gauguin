package org.piepmeyer.gauguin.grid

class GridSolveService(
    private val grid: Grid
) {
    fun revealSelectedCage() {
        grid.selectedCell?.let {
            it.cage().cells.forEach { revealCell(it) }
            it.isSelected = false
        }
    }

    fun solveGrid() {
        grid.cells.forEach { revealCell(it) }

        grid.selectedCell?.let {
            it.isSelected = false
        }
    }

    fun revealSelectedCell() {
        grid.selectedCell?.let { revealCell(it) }
    }

    private fun revealCell(cell: GridCell) {
        if (!cell.isUserValueCorrect) {
            cell.clearPossibles()
            cell.setUserValueIntern(cell.value)
            cell.isCheated = true
        }
    }
}
