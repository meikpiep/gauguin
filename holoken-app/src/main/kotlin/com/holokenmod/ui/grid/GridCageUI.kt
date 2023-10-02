package com.holokenmod.ui.grid

import android.graphics.Canvas
import com.holokenmod.grid.GridCage

class GridCageUI(
    private val grid: GridUI,
    private val cage: GridCage,
    private val paintHolder: GridPaintHolder
) {

    private var westPixel: Float = 0f
    private var northPixel: Float = 0f

    fun onDraw(canvas: Canvas, cellSize: Float, padding: Pair<Int, Int>) {
        westPixel = padding.first + cellSize * cage.getCell(0).column + GridUI.BORDER_WIDTH
        northPixel = padding.second + cellSize * cage.getCell(0).row + GridUI.BORDER_WIDTH

        drawCageText(canvas, cellSize)
    }

    private fun drawCageText(canvas: Canvas, cellSize: Float) {
        if (cage.cageText.isEmpty()) {
            return
        }

        val paint = paintHolder.cageTextPaint(cage, grid.isPreviewMode)

        val cageTextSize = (cellSize / 3.5).toInt()

        paint.textSize = cageTextSize.toFloat()

        canvas.drawText(
            cage.cageText,
            westPixel + 4,
            northPixel + cageTextSize,
            paint
        )
    }

}
