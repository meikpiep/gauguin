package com.holokenmod.grid

class GridSolveService(
    private val grid: Grid
) {
    fun solveSelectedCage() {
        grid.selectedCell?.let {
            it.cage?.cells?.forEach { cell ->
                if (!cell.isUserValueCorrect) {
                    cell.clearPossibles()
                    cell.setUserValueIntern(cell.value)
                    cell.isCheated = true
                }
            }
            it.isSelected = false
            it.cage?.setSelected(false)
        }
    }

    fun solveGrid() {
        for (cell in grid.cells) {
            if (!cell.isUserValueCorrect) {
                cell.clearPossibles()
                cell.setUserValueIntern(cell.value)
                cell.isCheated = true
            }
        }
        grid.selectedCell?.let {
            it.isSelected = false
            it.cage?.setSelected(false)
        }
    }

    fun revealSelectedCell() {
        val selectedCell = grid.selectedCell ?: return

        selectedCell.setUserValueIntern(selectedCell.value)
        selectedCell.isCheated = true
    }
}
