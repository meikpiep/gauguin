package org.piepmeyer.gauguin.ui.grid

import android.graphics.Canvas
import android.graphics.Paint
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.options.ApplicationPreferencesImpl

class GridCellUIPossibleNumbersDrawer(
    private val cellUI: GridCellUI,
    private val paintHolder: GridPaintHolder
): KoinComponent {
    private val applicationPreferences: ApplicationPreferencesImpl by inject()
    private val cell = cellUI.cell

    fun drawPossibleNumbers(
        canvas: Canvas,
        possibleDigits: Set<Int>,
        cellSize: Float,
        layoutDetails: GridLayoutDetails
    ) {
        if (cell.possibles.isEmpty()) return

        val possiblesPaint: Paint = paintHolder.possiblesPaint(cell)

        if (possibleDigits.size <= 9 && possibleDigits.max() <= 9 && applicationPreferences.show3x3Pencils()) {
            drawPossibleNumbersWithFixedGrid(canvas, possibleDigits, cellSize, possiblesPaint, layoutDetails)
        } else {
            drawPossibleNumbersDynamically(canvas, cellSize, possiblesPaint, layoutDetails)
        }
    }

    private fun drawPossibleNumbersDynamically(
        canvas: Canvas,
        cellSize: Float,
        paint: Paint,
        layoutDetails: GridLayoutDetails
    ) {
        val possiblesLines = adaptTextSize(paint, cellSize, layoutDetails)

        var index = 0
        val metrics = paint.fontMetricsInt
        val lineHeigth = -metrics.ascent + metrics.leading + metrics.descent

        possiblesLines.forEach {
            canvas.drawText(
                it,
                cellUI.westPixel + layoutDetails.possibleNumbersMarginX(),
                cellUI.northPixel + cellSize - layoutDetails.possibleNumbersMarginY() - lineHeigth * index,
                paint
            )
            index++
        }
    }

    private fun adaptTextSize(
        paint: Paint,
        cellSize: Float,
        layoutDetails: GridLayoutDetails
    ): List<String> {
        for(textDivider in listOf(4f, 4.25f, 4.5f, 4.75f)) {
            paint.textSize = (cellSize / textDivider).toInt().toFloat()
            val possiblesLines = calculatePossibleLines(paint, cellSize, layoutDetails)

            if (possiblesLines.size <= 2) {
                return possiblesLines
            }
        }

        paint.textSize = (cellSize / 5.0f).toInt().toFloat()
        return calculatePossibleLines(paint, cellSize, layoutDetails)
    }

    private fun calculatePossibleLines(
        paint: Paint,
        cellSize: Float,
        layoutDetails: GridLayoutDetails
    ): List<String> {
        if (cellSize < 35) {
            return listOf("...")
        }

        val possiblesLines = mutableListOf<MutableSet<Int>>()

        //adds all possible to one line
        var currentLine = cell.possibles.sorted().toMutableSet()
        possiblesLines += currentLine
        var currentLineText = getPossiblesLineText(currentLine)

        while (paint.measureText(currentLineText) > cellSize - 2 * layoutDetails.possibleNumbersMarginX()) {
            val newLine = mutableSetOf<Int>()
            possiblesLines += newLine
            while (paint.measureText(currentLineText) > cellSize - 2 * layoutDetails.possibleNumbersMarginX()) {
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

    private fun drawPossibleNumbersWithFixedGrid(
        canvas: Canvas,
        possibleDigits: Set<Int>,
        cellSize: Float,
        paint: Paint,
        layoutDetails: GridLayoutDetails
    ) {
        paint.textSize = (cellSize / 4.75).toInt().toFloat()
        val xOffset = layoutDetails.possibleNumbersMarginX() * 2
        val yOffset = (cellSize / 1.9).toInt() + 1

        for (possible in cell.possibles) {
            val index = possibleDigits.indexOf(possible)

            canvas.drawText(
                possible.toString(),
                cellUI.westPixel + xOffset + index % 3 * layoutDetails.possiblesFixedGridDistanceX(),
                cellUI.northPixel + yOffset + index / 3 * layoutDetails.possiblesFixedGridDistanceY(),
                paint
            )
        }
    }

    private fun getPossiblesLineText(possibles: Set<Int>): String {
        return possibles.joinToString("|")
    }
}