package com.holokenmod.ui.grid

import android.graphics.Canvas
import android.graphics.Path
import com.holokenmod.creation.cage.BorderInfo
import com.holokenmod.grid.GridCage

class GridCageUI(
    private val grid: GridUI,
    private val cage: GridCage,
    private val paintHolder: GridPaintHolder
) {

    private var westPixel: Float = 0f
    private var northPixel: Float = 0f

    fun onDraw(canvas: Canvas, cellSize: Float) {
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
            westPixel + 4 + 8,
            northPixel + cageTextSize + 8,
            paint
        )
    }

    fun drawCageBackground(canvas: Canvas, cellSize: Float, padding: Pair<Int, Int>) {
        westPixel = padding.first + cellSize * cage.getCell(0).column + GridUI.BORDER_WIDTH
        northPixel = padding.second + cellSize * cage.getCell(0).row + GridUI.BORDER_WIDTH

        val paint = paintHolder.gridPaint(cage, grid.grid, cellSize)

        val offsetDistance = 5

        var pixelX = westPixel + offsetDistance
        var pixelY = northPixel + offsetDistance

        val path = Path()
        path.moveTo(pixelX, pixelY)

        cage.cageType.borderInfos.forEach{
            val length = it.length * cellSize - it.offset * offsetDistance

            when (it.direction) {
                BorderInfo.Direction.UP -> pixelY -= length
                BorderInfo.Direction.DOWN -> pixelY += length
                BorderInfo.Direction.LEFT -> pixelX -= length
                BorderInfo.Direction.RIGHT -> pixelX += length
            }

            path.lineTo(pixelX, pixelY)
        }

        path.close()

        canvas.drawPath(path, paint)
    }
}
