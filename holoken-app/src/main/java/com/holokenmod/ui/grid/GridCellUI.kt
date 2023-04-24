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

    fun onDraw(canvas: Canvas, cellSize: Float) {
        this.cellSize = cellSize

        westPixel = cellSize * cell.column + GridUI.BORDER_WIDTH
        northPixel = cellSize * cell.row + GridUI.BORDER_WIDTH

        drawCellBackground(canvas)
        borderDrawer.drawBorders(canvas)

        drawCellValue(canvas, cellSize)

        if (cell.possibles.isNotEmpty()) {
            possibleNumbersDrawer.drawPossibleNumbers(canvas, cellSize)
        }
    }

    private fun drawCellValue(canvas: Canvas, cellSize: Float) {
        if (cell.isUserValueSet) {
            val paint: Paint = if (cell.isSelected) {
                paintHolder.textOfSelectedCellPaint
            } else if (cell.isShowWarning || cell.isCheated) {
                paintHolder.mWarningTextPaint
            } else {
                paintHolder.mValuePaint
            }
            val textSize = (cellSize * 3 / 4).toInt()
            paint.textSize = textSize.toFloat()
            val leftOffset: Float = if (cell.userValue <= 9) {
                cellSize / 2 - textSize / 4
            } else {
                cellSize / 2 - textSize / 2
            }
            val topOffset = cellSize / 2 + textSize * 2 / 5
            canvas.drawText(
                "" + cell.userValue, westPixel + leftOffset,
                northPixel + topOffset, paint
            )
        }
    }

    private fun drawCellBackground(canvas: Canvas) {
        val paint = cellBackgroundPaint
        if (paint != null) {
            canvas.drawRect(
                westPixel + 1,
                northPixel + 1,
                eastPixel - 1,
                southPixel - 1,
                paint
            )
        }
    }

    private val cellBackgroundPaint: Paint?
        get() {
            if (cell.isSelected) {
                return paintHolder.mSelectedPaint
            }
            if (cell.isLastModified) {
                return paintHolder.mLastModifiedPaint
            }
            if (cell.isCheated) {
                return paintHolder.mCheatedPaint
            }
            return if (cell.isInvalidHighlight) {
                paintHolder.mWarningPaint
            } else null
        }

}