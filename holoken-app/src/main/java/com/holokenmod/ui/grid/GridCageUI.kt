package com.holokenmod.ui.grid

import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.graphics.ColorUtils
import com.holokenmod.grid.GridCage

class GridCageUI(
    private val grid: GridUI,
    val cage: GridCage,
    private val paintHolder: GridPaintHolder
) {

    private var westPixel: Float = 0f
    private var northPixel: Float = 0f

    fun onDraw(canvas: Canvas, cellSize: Float) {
        westPixel = cellSize * cage.getCell(0).column + GridUI.BORDER_WIDTH
        northPixel = cellSize * cage.getCell(0).row + GridUI.BORDER_WIDTH

        drawCageText(canvas, cellSize)
    }

    private fun drawCageText(canvas: Canvas, cellSize: Float) {
        if (cage.cageText.isEmpty()) {
            return
        }

        val paint: Paint?

        if (grid.isPreviewMode) {
            val hsl = FloatArray(3)
            ColorUtils.colorToHSL(paintHolder.mCageTextPaint.color, hsl)
            hsl[1] = hsl[1] * 0.35f
            paint = Paint()
            paint.color = ColorUtils.HSLToColor(hsl)
        } else if (cage.getCell(0).isSelected || cage.getCell(0).isLastModified) {
            paint = paintHolder.textOfSelectedCellPaint
        } else {
            paint = paintHolder.mCageTextPaint
        }
        val cageTextSize = (cellSize / 3).toInt()

        paint.textSize = cageTextSize.toFloat()

        canvas.drawText(
            cage.cageText,
            westPixel + 4,
            northPixel + cageTextSize,
            paint
        )
    }

}
