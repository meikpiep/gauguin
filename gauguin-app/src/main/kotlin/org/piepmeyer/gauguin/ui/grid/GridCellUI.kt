package org.piepmeyer.gauguin.ui.grid

import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.RectF
import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.options.NumeralSystem
import kotlin.math.min

class GridCellUI(
    val cell: GridCell,
    private val paintHolder: GridPaintHolder,
) {
    var westPixel: Float
        private set
    var northPixel: Float
        private set
    private val eastPixel: Float
        get() = westPixel + cellSize.first
    private val southPixel: Float
        get() = northPixel + cellSize.second

    private val possibleNumbersDrawer = GridCellUIPossibleNumbersDrawer(this, paintHolder)
    private var cellSize = Pair(0f, 0f)

    init {
        westPixel = 0f
        northPixel = 0f
    }

    fun onDraw(
        canvas: Canvas,
        grid: GridUI,
        cellSize: Pair<Float, Float>,
        padding: Pair<Int, Int>,
        layoutDetails: GridLayoutDetails,
        fastFinishMode: Boolean,
        showBadMaths: Boolean,
        markDuplicatedInRowOrColumn: Boolean,
    ) {
        this.cellSize = cellSize
        this.westPixel = padding.first + cellSize.first * cell.column + GridUI.BORDER_WIDTH
        this.northPixel = padding.second + cellSize.second * cell.row + GridUI.BORDER_WIDTH

        drawCellBackground(canvas, layoutDetails, showBadMaths, markDuplicatedInRowOrColumn, fastFinishMode)

        if (grid.grid.getCellAt(cell.row, cell.column + 1) != null &&
            cell.cage == grid.grid.getCage(cell.row, cell.column + 1)
        ) {
            canvas.drawLine(
                westPixel + cellSize.first,
                northPixel + layoutDetails.innerGridWidth(),
                westPixel + cellSize.first,
                northPixel + cellSize.second - layoutDetails.innerGridWidth(),
                layoutDetails.innerGridPaint(),
            )
        }
        if (grid.grid.getCellAt(cell.row + 1, cell.column) != null &&
            cell.cage == grid.grid.getCage(cell.row + 1, cell.column)
        ) {
            canvas.drawLine(
                westPixel + layoutDetails.innerGridWidth(),
                northPixel + cellSize.second,
                westPixel + cellSize.first - layoutDetails.innerGridWidth(),
                northPixel + cellSize.second,
                layoutDetails.innerGridPaint(),
            )
        }
    }

    fun onDrawForeground(
        canvas: Canvas,
        cellSize: Pair<Float, Float>,
        grid: GridUI,
        padding: Pair<Int, Int>,
        layoutDetails: GridLayoutDetails,
        fastFinishMode: Boolean,
        numeralSystem: NumeralSystem,
    ) {
        this.cellSize = cellSize
        this.westPixel = padding.first + cellSize.first * cell.column + GridUI.BORDER_WIDTH
        this.northPixel = padding.second + cellSize.second * cell.row + GridUI.BORDER_WIDTH

        drawSelectionRect(canvas, layoutDetails)
        drawCellValue(canvas, cellSize, fastFinishMode, numeralSystem)

        if (cell.possibles.isNotEmpty()) {
            possibleNumbersDrawer.drawPossibleNumbers(
                canvas,
                grid.grid.variant.possibleDigits,
                cellSize,
                layoutDetails,
                fastFinishMode,
                numeralSystem,
            )
        }
    }

    private fun drawCellValue(
        canvas: Canvas,
        cellSize: Pair<Float, Float>,
        fastFinishMode: Boolean,
        numeralSystem: NumeralSystem,
    ) {
        if (!cell.isUserValueSet) {
            return
        }

        val number = numeralSystem.displayableString(cell.userValue)

        val averageCellLength = min(cellSize.first, cellSize.second)

        val paint: Paint = paintHolder.cellValuePaint(cell, fastFinishMode)
        val textSize =
            when (number.length) {
                1 -> (averageCellLength * 3f / 4)
                2 -> (averageCellLength * 5f / 8)
                else -> (averageCellLength * 7f / 6 / number.length)
            }

        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER
        paint.isFakeBoldText = (number.length > 2)

        val topOffset = cellSize.second / 2 + textSize * 2 / 5

        canvas.drawText(
            number,
            westPixel + cellSize.first / 2,
            northPixel + topOffset,
            paint,
        )
    }

    private fun drawCellBackground(
        canvas: Canvas,
        layoutDetails: GridLayoutDetails,
        showBadMaths: Boolean,
        markDuplicatedInRowOrColumn: Boolean,
        fastFinishMode: Boolean,
    ) {
        val badMathInCage = showBadMaths && !cell.cage().isUserMathCorrect()

        val paint = paintHolder.cellBackgroundPaint(cell, badMathInCage, markDuplicatedInRowOrColumn, fastFinishMode) ?: return

        drawCellRect(layoutDetails, paint, canvas)
    }

    private fun drawSelectionRect(
        canvas: Canvas,
        layoutDetails: GridLayoutDetails,
    ) {
        val paint = paintHolder.cellForegroundPaint(cell) ?: return

        drawCellRect(layoutDetails, paint, canvas)
    }

    private fun drawCellRect(
        layoutDetails: GridLayoutDetails,
        paint: Paint,
        canvas: Canvas,
    ) {
        val offsetDistance = layoutDetails.offsetDistance()

        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeWidth = offsetDistance.toFloat()
        paint.pathEffect = CornerPathEffect(layoutDetails.gridPaintRadius())

        canvas.drawRect(
            RectF(
                westPixel + offsetDistance,
                northPixel + offsetDistance,
                eastPixel - offsetDistance,
                southPixel - offsetDistance,
            ),
            paint,
        )
    }
}
