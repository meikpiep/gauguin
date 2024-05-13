package org.piepmeyer.gauguin.ui.grid

import android.graphics.CornerPathEffect
import android.graphics.Paint
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCage
import kotlin.math.max

class GridLayoutDetails(
    private val cellSize: Pair<Float, Float>,
    private val painterHolder: GridPaintHolder,
) {
    fun averageLengthOfCell(): Float = (cellSize.first + cellSize.second) / 2

    fun gridPaint(
        cage: GridCage,
        grid: Grid,
        showBadMaths: Boolean,
    ): Paint {
        val cageSelected = grid.isActive && grid.selectedCell?.cage == cage

        val badMathCage = !cage.isUserMathCorrect() && showBadMaths

        val paint =
            if (badMathCage) {
                painterHolder.warningGridPaint
            } else if (cageSelected) {
                painterHolder.selectedGridPaint()
            } else {
                painterHolder.gridPaint()
            }

        paint.pathEffect = CornerPathEffect(gridPaintRadius())
        paint.strokeWidth =
            if (cageSelected || badMathCage) {
                gridSelectedPaintStrokeWidth()
            } else {
                gridPaintStrokeWidth()
            }

        return paint
    }

    fun innerGridPaint(): Paint {
        return painterHolder.innerGridPaint().apply { strokeWidth = gridPaintStrokeWidth() / 2 }
    }

    fun gridPaintRadius(): Float = 0.21f * averageLengthOfCell()

    fun possiblesFixedGridDistanceX(): Float = 0.25f * cellSize.first

    fun possiblesFixedGridDistanceYUpToSixValues(): Float = 0.22f * cellSize.second

    fun possiblesFixedGridDistanceYFromSevenValuesOn(): Float = 0.19f * cellSize.second

    fun yOffsetUpToSixValues(): Int = (cellSize.second / 1.6).toInt() + 1

    fun yOffsetFromSevenOn(): Int = (cellSize.second / 1.9).toInt() + 1

    private fun gridPaintStrokeWidth(): Float = max(0.02f * averageLengthOfCell(), 1f)

    private fun gridSelectedPaintStrokeWidth(): Float = max(0.03f * averageLengthOfCell(), 1f)

    fun offsetDistance(): Int = max(5f / 119f * averageLengthOfCell(), 1f).toInt()

    fun innerGridWidth(): Int = max(8f / 119f * averageLengthOfCell(), 1f).toInt()

    fun possibleNumbersMarginX(): Int = max(13f / 119f * averageLengthOfCell(), 1f).toInt()

    fun possibleNumbersMarginY(): Int = max(15f / 119f * averageLengthOfCell(), 1f).toInt()

    fun cageTextMarginX(): Int = max(12f / 119f * averageLengthOfCell(), 1f).toInt()

    fun cageTextMarginY(): Int = max(10f / 119f * averageLengthOfCell(), 1f).toInt()

    fun cageTextSize(): Float = averageLengthOfCell() / 3.5f
}
