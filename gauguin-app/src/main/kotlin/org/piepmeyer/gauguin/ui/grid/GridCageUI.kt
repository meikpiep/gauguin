package org.piepmeyer.gauguin.ui.grid

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.creation.cage.BorderInfo
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCageAction
import org.piepmeyer.gauguin.options.NumeralSystem

class GridCageUI(
    private val grid: GridUI,
    private val cage: GridCage,
    private val paintHolder: GridPaintHolder,
    showOperators: Boolean,
    numeralSystem: NumeralSystem,
) {
    private val cageText = retrieveCageText(showOperators, numeralSystem)

    private var westPixel: Float = 0f
    private var northPixel: Float = 0f

    private fun retrieveCageText(
        showOperators: Boolean,
        numeralSystem: NumeralSystem,
    ): String {
        val displayableNumber = numeralSystem.displayableString(cage.result)

        val textRessourceId =
            if (showOperators) {
                when (cage.action) {
                    GridCageAction.ACTION_NONE -> R.string.game_grid_cage_math_result_single_cell
                    GridCageAction.ACTION_ADD -> R.string.game_grid_cage_math_result_add_with_operator
                    GridCageAction.ACTION_SUBTRACT -> R.string.game_grid_cage_math_result_subtract_with_operator
                    GridCageAction.ACTION_MULTIPLY -> R.string.game_grid_cage_math_result_multiply_with_operator
                    GridCageAction.ACTION_DIVIDE -> R.string.game_grid_cage_math_result_divide_with_operator
                }
            } else {
                R.string.game_grid_cage_math_result_no_operator_shown
            }

        return grid.resources.getString(textRessourceId, displayableNumber)
    }

    fun drawCageText(
        canvas: Canvas,
        cellSize: Pair<Float, Float>,
        layoutDetails: GridLayoutDetails,
        showBadMaths: Boolean,
        markDuplicatedInRowOrColumn: Boolean,
        fastFinishMode: Boolean,
    ) {
        val paint = paintHolder.cageTextPaint(cage, grid.isPreviewMode, fastFinishMode)

        var scale = 1f

        val boundingRect = Rect()
        paint.textSize = layoutDetails.cageTextSize()
        paint.getTextBounds(cageText, 0, cageText.length, boundingRect)

        val maximumWidth =
            if (grid.grid.belongsCellToTheEastOfFirstCellToSameCage(cage, 1)) {
                if (grid.grid.belongsCellToTheEastOfFirstCellToSameCage(cage, 2)) {
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
            paint.getTextBounds(cageText, 0, cageText.length, boundingRect)
        }

        val firstCell = cage.cells.first()

        val badMathInCage = showBadMaths && !firstCell.cage().isUserMathCorrect()

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = layoutDetails.cageTextStrokeWidth()
        val foregroundColor = paint.color
        paint.color = paintHolder.cellBackgroundPaint(firstCell, badMathInCage, markDuplicatedInRowOrColumn, fastFinishMode)?.color
            ?: paintHolder.backgroundPaint().color
        paint.alpha = 127

        // draw with increased width and background color
        drawCageText(canvas, layoutDetails, paint)

        paint.style = Paint.Style.FILL
        paint.strokeWidth = 0.0f
        paint.color = foregroundColor
        paint.alpha = 255

        // draw text with foreground color
        drawCageText(canvas, layoutDetails, paint)
    }

    private fun drawCageText(
        canvas: Canvas,
        layoutDetails: GridLayoutDetails,
        paint: Paint,
    ) {
        canvas.drawText(
            cageText,
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
