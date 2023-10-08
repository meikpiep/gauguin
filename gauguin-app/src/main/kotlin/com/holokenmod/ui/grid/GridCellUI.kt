package com.holokenmod.ui.grid

import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.RectF
import com.holokenmod.grid.GridCell

class GridCellUI(
    val cell: GridCell,
    private val paintHolder: GridPaintHolder
) {
    var westPixel: Float
        private set
    var northPixel: Float
        private set
    val southPixel: Float
        get() = northPixel + cellSize
    val eastPixel: Float
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
        padding: Pair<Int, Int>
    ) {
        this.cellSize = cellSize
        this.westPixel = padding.first + cellSize * cell.column + GridUI.BORDER_WIDTH
        this.northPixel = padding.second + cellSize * cell.row + GridUI.BORDER_WIDTH

        drawCellBackground(canvas)

        if (cell.cage() == grid.grid.getCage(cell.row, cell.column + 1)) {
            canvas.drawLine(westPixel + cellSize,
                northPixel + 8,
                westPixel + cellSize,
                northPixel + cellSize - 8,
                paintHolder.innerGridPaint(cellSize))
        }
        if (cell.cage() == grid.grid.getCage(cell.row + 1, cell.column)) {
            canvas.drawLine(westPixel + 8,
                northPixel + cellSize,
                westPixel + cellSize - 8,
                northPixel + cellSize,
                paintHolder.innerGridPaint(cellSize))
        }

        drawCellValue(canvas, cellSize)

        if (cell.possibles.isNotEmpty()) {
            possibleNumbersDrawer.drawPossibleNumbers(canvas, cellSize)
        }
    }

    private fun drawCellValue(canvas: Canvas, cellSize: Float) {
        if (!cell.isUserValueSet) {
            return
        }

        val paint: Paint = paintHolder.cellValuePaint(cell)
        val textSize = (cellSize * 3 / 4)
        paint.textSize = textSize

        val leftOffset = if (cell.userValue <= 9) {
            cellSize / 2 - textSize / 4
        } else {
            cellSize / 2 - textSize / 2
        }

        val topOffset = cellSize / 2 + textSize * 2 / 5

        canvas.drawText(
            cell.userValue.toString(),
            westPixel + leftOffset,
            northPixel + topOffset,
            paint
        )
    }

    private fun drawCellBackground(canvas: Canvas) {
        val paint = paintHolder.cellBackgroundPaint(cell) ?: return

        val offsetDistance = 5

        paint.strokeJoin = Paint.Join.ROUND
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        paint.pathEffect = CornerPathEffect(0.21f * cellSize)

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
