package com.holokenmod.ui.grid

import android.graphics.Canvas
import android.graphics.Paint
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

    private val borderDrawer = GridCellUIBorderDrawer(this, paintHolder)
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

    fun onDraw(canvas: Canvas, cellSize: Float, padding: Pair<Int, Int>) {
        this.cellSize = cellSize
        this.westPixel = padding.first + cellSize * cell.column + GridUI.BORDER_WIDTH
        this.northPixel = padding.second + cellSize * cell.row + GridUI.BORDER_WIDTH

        drawCellBackground(canvas)
        borderDrawer.drawBorders(canvas)

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
        paintHolder.cellBackgroundPaint(cell)?.let {
            canvas.drawRect(
                westPixel + 1,
                northPixel + 1,
                eastPixel - 1,
                southPixel - 1,
                it
            )
        }
    }
}