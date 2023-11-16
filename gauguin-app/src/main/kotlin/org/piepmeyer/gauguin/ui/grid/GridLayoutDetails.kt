package org.piepmeyer.gauguin.ui.grid

import android.graphics.CornerPathEffect
import android.graphics.Paint
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import kotlin.math.max

class GridLayoutDetails(
    private val cellSize: Float,
    private val painterHolder: GridPaintHolder
) {

    fun gridPaint(cage: GridCage, grid: Grid): Paint {
        val paint = if (!cage.isUserMathCorrect() && grid.options.showBadMaths) {
            painterHolder.warningGridPaint
        } else if (grid.isActive && grid.selectedCell?.cage == cage) {
            painterHolder.selectedGridPaint()
        } else {
            painterHolder.gridPaint()
        }

        paint.pathEffect = CornerPathEffect(gridPaintRadius())
        paint.strokeWidth = gridPaintStrokeWidth()

        return paint
    }

    fun innerGridPaint(): Paint {
        return painterHolder.innerGridPaint().apply { strokeWidth = gridPaintStrokeWidth() / 2 }
    }

    fun gridPaintRadius(): Float = 0.21f * cellSize

    fun possiblesFixedGridDistanceX(): Float = 0.25f * cellSize
    fun possiblesFixedGridDistanceY(): Float = 0.21f * cellSize

    private fun gridPaintStrokeWidth(): Float = max(0.02f * cellSize, 1f)
    fun offsetDistance(): Int = max(5f / 119f * cellSize, 1f).toInt()
    fun innerGridWidth(): Int = max(8f / 119f * cellSize, 1f).toInt()
    fun possibleNumbersMarginX(): Int = max(13f / 119f * cellSize, 1f).toInt()
    fun possibleNumbersMarginY(): Int = max(15f / 119f * cellSize, 1f).toInt()
    fun cageTextMarginX(): Int = max(12f / 119f * cellSize, 1f).toInt()

    fun cageTextMarginY(): Int = max(8f / 119f * cellSize, 1f).toInt()
    fun cageTextSize(): Float = cellSize / 3.5f
}
