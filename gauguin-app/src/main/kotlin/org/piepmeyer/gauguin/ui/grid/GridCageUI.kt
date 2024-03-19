package org.piepmeyer.gauguin.ui.grid

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import org.piepmeyer.gauguin.creation.cage.BorderInfo
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.options.NumeralSystem

class GridCageUI(
    private val grid: GridUI,
    private val cage: GridCage,
    private val paintHolder: GridPaintHolder,
) {
    private var westPixel: Float = 0f
    private var northPixel: Float = 0f

    fun drawCageText(
        canvas: Canvas,
        cellSize: Pair<Float, Float>,
        layoutDetails: GridLayoutDetails,
        fastFinishMode: Boolean,
        showOperators: Boolean,
        numeralSystem: NumeralSystem,
    ) {
        val operation =
            if (showOperators) {
                cage.action.operationDisplayName
            } else {
                ""
            }

        val text = numeralSystem.displayableString(cage.result) + operation
        val paint = paintHolder.cageTextPaint(cage, grid.isPreviewMode, fastFinishMode)

        var scale = 1f

        val boundingRect = Rect()
        paint.textSize = layoutDetails.cageTextSize()
        paint.getTextBounds(text, 0, text.length, boundingRect)

        val maximumWidth =
            if (cage.belongsCellToTheEastOfFirstCellToCage(1)) {
                if (cage.belongsCellToTheEastOfFirstCellToCage(2)) {
                    cellSize.first * 3
                } else {
                    cellSize.first * 2
                }
            } else {
                cellSize.first
            }

        while (scale > 0.3 && boundingRect.width() > (maximumWidth - 2 * layoutDetails.cageTextMarginX())) {
            scale -= 0.1f
            paint.textSize = layoutDetails.cageTextSize() * scale
            paint.getTextBounds(text, 0, text.length, boundingRect)
        }

        canvas.drawText(
            text,
            westPixel + layoutDetails.cageTextMarginX(),
            northPixel - paint.fontMetricsInt.ascent + layoutDetails.cageTextMarginY(),
            paint,
        )
    }

    fun drawCageBackground(
        canvas: Canvas,
        cellSize: Pair<Float, Float>,
        padding: Pair<Int, Int>,
        layoutDetails: GridLayoutDetails,
        showBadMaths: Boolean,
    ) {
        westPixel = padding.first + cellSize.first * cage.getCell(0).column + GridUI.BORDER_WIDTH
        northPixel = padding.second + cellSize.second * cage.getCell(0).row + GridUI.BORDER_WIDTH

        val paint = layoutDetails.gridPaint(cage, grid.grid, showBadMaths)

        val path = createCagePath(cellSize, layoutDetails)

        canvas.drawPath(path, paint)
    }

    private fun createCagePath(
        cellSize: Pair<Float, Float>,
        layoutDetails: GridLayoutDetails,
    ): Path {
        var pixelX = westPixel + layoutDetails.offsetDistance()
        var pixelY = northPixel + layoutDetails.offsetDistance()

        val path = Path()
        path.moveTo(pixelX, pixelY)

        cage.cageType.borderInfos.forEach {
            val lengthOfCell =
                when (it.direction) {
                    BorderInfo.Direction.LEFT, BorderInfo.Direction.RIGHT -> cellSize.first
                    BorderInfo.Direction.UP, BorderInfo.Direction.DOWN -> cellSize.second
                }

            val length = it.length * lengthOfCell - it.offset * layoutDetails.offsetDistance()

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
