package org.piepmeyer.gauguin.ui.grid

import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.RectF
import org.piepmeyer.gauguin.grid.GridCell
import org.piepmeyer.gauguin.options.NumeralSystem

class GridCellUI(
    val cell: GridCell,
    private val paintHolder: GridPaintHolder
) {
    var westPixel: Float
        private set
    var northPixel: Float
        private set
    private val southPixel: Float
        get() = northPixel + cellSize
    private val eastPixel: Float
        get() = westPixel + cellSize

    private val possibleNumbersDrawer = GridCellUIPossibleNumbersDrawer(this, paintHolder)
    private var cellSize = 0f

    init {
        westPixel = 0f
        northPixel = 0f
    }

    override fun toString(): String {
        return "<cell:" + cell.cellNumber + " col:" + cell.column +
                " row:" + cell.row + " posX:" + westPixel + " posY:" +
                northPixel + " val:" + cell.value + ", userval: " + cell
            .userValue + ">"
    }

    fun onDraw(
        canvas: Canvas,
        grid: GridUI,
        cellSize: Float,
        padding: Pair<Int, Int>,
        layoutDetails: GridLayoutDetails,
        fastFinishMode: Boolean,
        numeralSystem: NumeralSystem
    ) {
        this.cellSize = cellSize
        this.westPixel = padding.first + cellSize * cell.column + GridUI.BORDER_WIDTH
        this.northPixel = padding.second + cellSize * cell.row + GridUI.BORDER_WIDTH

        drawCellBackground(canvas, layoutDetails, fastFinishMode)

        if (grid.grid.getCellAt(cell.row, cell.column + 1) != null &&
            cell.cage == grid.grid.getCage(cell.row, cell.column + 1)) {
            canvas.drawLine(westPixel + cellSize,
                northPixel + layoutDetails.innerGridWidth(),
                westPixel + cellSize,
                northPixel + cellSize - layoutDetails.innerGridWidth(),
                layoutDetails.innerGridPaint())
        }
        if (grid.grid.getCellAt(cell.row + 1, cell.column) != null &&
            cell.cage == grid.grid.getCage(cell.row + 1, cell.column)) {
            canvas.drawLine(westPixel + layoutDetails.innerGridWidth(),
                northPixel + cellSize,
                westPixel + cellSize - layoutDetails.innerGridWidth(),
                northPixel + cellSize,
                layoutDetails.innerGridPaint())
        }

        drawCellValue(canvas, cellSize, fastFinishMode, numeralSystem)

        if (cell.possibles.isNotEmpty()) {
            possibleNumbersDrawer.drawPossibleNumbers(
                canvas,
                grid.grid.variant.possibleDigits,
                cellSize,
                layoutDetails,
                fastFinishMode,
                numeralSystem
            )
        }
    }

    private fun drawCellValue(
        canvas: Canvas,
        cellSize: Float,
        fastFinishMode: Boolean,
        numeralSystem: NumeralSystem
    ) {
        if (!cell.isUserValueSet) {
            return
        }

        val number = numeralSystem.displayableString(cell.userValue)

        val paint: Paint = paintHolder.cellValuePaint(cell, fastFinishMode)
        val textSize = when (number.length) {
            1 -> (cellSize * 3f / 4)
            2 -> (cellSize * 5f / 8)
            else -> (cellSize * 7f / 6 / number.length)
        }

        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER
        paint.isFakeBoldText = (number.length > 2)

        val topOffset = cellSize / 2 + textSize * 2 / 5

        canvas.drawText(
            number,
            westPixel + cellSize / 2,
            northPixel + topOffset,
            paint
        )
    }

    private fun drawCellBackground(canvas: Canvas, layoutDetails: GridLayoutDetails, fastFinishMode: Boolean) {
        val paint = paintHolder.cellBackgroundPaint(cell, fastFinishMode) ?: return

        val offsetDistance = layoutDetails.offsetDistance()

        paint.strokeJoin = Paint.Join.ROUND
//        paint.style = Paint.Style.STROKE
        paint.strokeWidth = offsetDistance.toFloat()
        paint.pathEffect = CornerPathEffect(layoutDetails.gridPaintRadius())

        canvas.drawRect(
            RectF(
                westPixel + offsetDistance,
                northPixel + offsetDistance,
                eastPixel - offsetDistance,
                southPixel - offsetDistance
            ),
            paint
        )
    }
}
