package org.piepmeyer.gauguin.ui.grid

import android.graphics.Canvas
import android.graphics.Path
import org.piepmeyer.gauguin.creation.cage.BorderInfo
import org.piepmeyer.gauguin.grid.GridCage

class GridCageUI(
    private val grid: GridUI,
    private val cage: GridCage,
    private val paintHolder: GridPaintHolder
) {

    private var westPixel: Float = 0f
    private var northPixel: Float = 0f

    fun onDraw(canvas: Canvas, layoutDetails: GridLayoutDetails) {
        drawCageText(canvas, layoutDetails)
    }

    private fun drawCageText(canvas: Canvas, layoutDetails: GridLayoutDetails) {
        if (cage.cageText.isEmpty()) {
            return
        }

        val paint = paintHolder.cageTextPaint(cage, grid.isPreviewMode)

        paint.textSize = layoutDetails.cageTextSize()

        canvas.drawText(
            cage.cageText,
            westPixel + layoutDetails.cageTextMarginX(),
            northPixel + layoutDetails.cageTextSize() + layoutDetails.cageTextMarginY(),
            paint
        )
    }

    fun drawCageBackground(canvas: Canvas, cellSize: Float, padding: Pair<Int, Int>, layoutDetails: GridLayoutDetails) {
        westPixel = padding.first + cellSize * cage.getCell(0).column + GridUI.BORDER_WIDTH
        northPixel = padding.second + cellSize * cage.getCell(0).row + GridUI.BORDER_WIDTH

        val paint = layoutDetails.gridPaint(cage, grid.grid)

        val path = createCagePath(cellSize, layoutDetails)

        canvas.drawPath(path, paint)
    }

    private fun createCagePath(cellSize: Float, layoutDetails: GridLayoutDetails): Path {
        var pixelX = westPixel + layoutDetails.offsetDistance()
        var pixelY = northPixel + layoutDetails.offsetDistance()

        val path = Path()
        path.moveTo(pixelX, pixelY)

        cage.cageType.borderInfos.forEach {
            val length = it.length * cellSize - it.offset * layoutDetails.offsetDistance()

            when (it.direction) {
                BorderInfo.Direction.UP -> pixelY -= length
                BorderInfo.Direction.DOWN -> pixelY += length
                BorderInfo.Direction.LEFT -> pixelX -= length
                BorderInfo.Direction.RIGHT -> pixelX += length
            }

            path.lineTo(pixelX, pixelY)
        }

        path.close()
        return path
    }
}
