package com.holokenmod.ui.grid

import android.graphics.Canvas
import android.graphics.Paint
import com.holokenmod.grid.GridCell
import com.holokenmod.options.ApplicationPreferencesImpl
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GridCellUIPossibleNumbersDrawer(
    private val cellUI: GridCellUI,
    private val paintHolder: GridPaintHolder
): KoinComponent {
    private val applicationPreferences: ApplicationPreferencesImpl by inject()
    private val cell: GridCell = cellUI.cell

    fun drawPossibleNumbers(canvas: Canvas, cellSize: Float) {
        val possiblesPaint: Paint = paintHolder.possiblesPaint(cell)

        if (applicationPreferences.show3x3Pencils()) {
            drawPossibleNumbersWithFixedGrid(canvas, cellSize, possiblesPaint)
        } else {
            drawPossibleNumbersDynamically(canvas, cellSize, possiblesPaint)
        }
    }

    private fun drawPossibleNumbersDynamically(canvas: Canvas, cellSize: Float, paint: Paint) {
        if (cell.possibles.isEmpty()) return

        val possiblesLines = calculatePossibleLines(paint, cellSize)

        var index = 0
        val metrics = paint.fontMetricsInt
        val lineHeigth = -metrics.ascent + metrics.leading + metrics.descent

        possiblesLines.forEach {
            canvas.drawText(
                it,
                cellUI.westPixel + 13,
                cellUI.northPixel + cellSize - 15 - lineHeigth * index,
                paint
            )
            index++
        }
    }

    private fun calculatePossibleLines(
        paint: Paint,
        cellSize: Float
    ): List<String> {
        if (cellSize < 35) {
            return listOf("...")
        }

        paint.textSize = (cellSize / 4).toInt().toFloat()
        val possiblesLines = mutableListOf<MutableSet<Int>>()

        //adds all possible to one line
        var currentLine = cell.possibles.sorted().toMutableSet()
        possiblesLines += currentLine
        var currentLineText = getPossiblesLineText(currentLine)

        while (paint.measureText(currentLineText) > cellSize - 26) {
            val newLine = mutableSetOf<Int>()
            possiblesLines += newLine
            while (paint.measureText(currentLineText) > cellSize - 26) {
                val firstDigitOfCurrentLine = currentLine.first()
                newLine.add(firstDigitOfCurrentLine)
                currentLine.remove(firstDigitOfCurrentLine)
                currentLineText = getPossiblesLineText(currentLine)
            }
            currentLine = newLine
            currentLineText = getPossiblesLineText(currentLine)
        }

        return possiblesLines.map { getPossiblesLineText(it) }
    }

    private fun drawPossibleNumbersWithFixedGrid(canvas: Canvas, cellSize: Float, paint: Paint) {
        paint.textSize = (cellSize / 4.5).toInt().toFloat()
        val xOffset = (cellSize / 3).toInt()
        val yOffset = (cellSize / 2).toInt() + 1
        val xScale = 0.21.toFloat() * cellSize
        val yScale = 0.21.toFloat() * cellSize
        for (possible in cell.possibles) {
            val xPos = cellUI.westPixel + xOffset + (possible - 1) % 3 * xScale
            val yPos = cellUI.northPixel + yOffset + (possible - 1) / 3 * yScale
            canvas.drawText(possible.toString(), xPos, yPos, paint)
        }
    }

    private fun getPossiblesLineText(possibles: Set<Int>): String {
        return possibles.joinToString("|")
    }
}