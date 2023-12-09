package org.piepmeyer.gauguin.ui.grid

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import org.piepmeyer.gauguin.creation.cage.BorderInfo
import org.piepmeyer.gauguin.grid.GridCage

class GridCageUI(
    private val grid: GridUI,
    private val cage: GridCage,
    private val paintHolder: GridPaintHolder
) {
    private var westPixel: Float = 0f
    private var northPixel: Float = 0f

    fun drawCageText(
        canvas: Canvas,
        cellSize: Float,
        layoutDetails: GridLayoutDetails,
        fastFinishMode: Boolean,
        showOperators: Boolean
    ) {
        val operation = if (showOperators) {
            cage.action.operationDisplayName
        } else {
            ""
        }

        val text = cage.result.toString() + operation
        val paint = paintHolder.cageTextPaint(cage, grid.isPreviewMode, fastFinishMode)

        var scale = 1f

        val boundingRect = Rect()
        paint.textSize = layoutDetails.cageTextSize()
        paint.getTextBounds(text, 0, text.length, boundingRect)

        val maximumWidth = if (cage.belongsCellToTheEastOfFirstCellToCage()) {
            cellSize * 2
        } else {
            cellSize
        }

        layoutDetails.cageTextMarginY()

        while (scale > 0.3 && boundingRect.width() > (maximumWidth - 2 * layoutDetails.cageTextMarginX())) {
            scale -= 0.1f
            paint.textSize = layoutDetails.cageTextSize() * scale
            paint.getTextBounds(text, 0, text.length, boundingRect)
        }

        canvas.drawText(
            text,
            westPixel + layoutDetails.cageTextMarginX(),
            northPixel - paint.fontMetricsInt.ascent + layoutDetails.cageTextMarginY(),
            paint
        )
    }

    fun drawCageBackground(canvas: Canvas, cellSize: Float, padding: Pair<Int, Int>, layoutDetails: GridLayoutDetails, showBadMaths: Boolean) {
        westPixel = padding.first + cellSize * cage.getCell(0).column + GridUI.BORDER_WIDTH
        northPixel = padding.second + cellSize * cage.getCell(0).row + GridUI.BORDER_WIDTH

        val paint = layoutDetails.gridPaint(cage, grid.grid, showBadMaths)

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
