package org.piepmeyer.gauguin.ui.grid

import android.graphics.Canvas
import android.graphics.Paint
import android.text.SpannableStringBuilder
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.graphics.withTranslation
import androidx.core.text.color
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

class GridCellUIPossibleNumbersDrawer(
    private val cellUI: GridCellUI,
    private val paintHolder: GridPaintHolder,
) : KoinComponent {
    private val applicationPreferences: ApplicationPreferences by inject()
    private val cell = cellUI.cell

    fun drawPossibleNumbers(
        canvas: Canvas,
        variant: GameVariant,
        invalidPossibles: List<Int>,
        cellSize: Pair<Float, Float>,
        layoutDetails: GridLayoutDetails,
        fastFinishMode: Boolean,
        numeralSystem: NumeralSystem,
    ) {
        if (cell.possibles.isEmpty()) return

        val possiblesPaint = paintHolder.possiblesPaint(cell, fastFinishMode)

        if (variant.possibleDigits.size <= 9 && variant.possibleDigits.max() <= 9 && applicationPreferences.show3x3Pencils) {
            drawPossibleNumbersWithFixedGrid(canvas, variant, possiblesPaint, invalidPossibles, layoutDetails, numeralSystem)
        } else {
            drawPossibleNumbersDynamically(canvas, cellSize, possiblesPaint, invalidPossibles, layoutDetails, numeralSystem)
        }
    }

    private fun drawPossibleNumbersDynamically(
        canvas: Canvas,
        cellSize: Pair<Float, Float>,
        paint: TextPaint,
        invalidPossibles: List<Int>,
        layoutDetails: GridLayoutDetails,
        numeralSystem: NumeralSystem,
    ) {
        val possiblesLines = adaptTextSize(paint, invalidPossibles, cellSize, layoutDetails, numeralSystem)

        drawPossiblesLines(paint, possiblesLines, canvas, layoutDetails, cellSize)
    }

    private fun drawPossiblesLines(
        paint: TextPaint,
        possiblesLines: List<SpannableStringBuilder>,
        canvas: Canvas,
        layoutDetails: GridLayoutDetails,
        cellSize: Pair<Float, Float>,
    ) {
        var index = 0
        val metrics = paint.fontMetricsInt
        val lineHeight = -metrics.ascent + metrics.leading + metrics.descent

        possiblesLines.forEach {
            if (it.isNotEmpty()) {
                val staticLayout: StaticLayout =
                    StaticLayout.Builder
                        .obtain(it, 0, it.length, paint, cellSize.first.toInt())
                        .build()

                canvas.withTranslation(
                    cellUI.westPixel + layoutDetails.possibleNumbersMarginX(),
                    cellUI.northPixel + cellSize.second - layoutDetails.possibleNumbersMarginY() - lineHeight * (index + 1),
                ) {
                    staticLayout.draw(this)
                }
            }
            index++
        }
    }

    private fun adaptTextSize(
        paint: Paint,
        invalidPossibles: List<Int>,
        cellSize: Pair<Float, Float>,
        layoutDetails: GridLayoutDetails,
        numeralSystem: NumeralSystem,
    ): List<SpannableStringBuilder> {
        val averageLengthOfCell = (cellSize.first + cellSize.second) / 2

        for (textDivider in listOf(4f, 4.25f, 4.5f, 4.75f, 5f, 5.25f, 5.5f, 5.75f)) {
            paint.textSize = (averageLengthOfCell / textDivider).toInt().toFloat()
            val possiblesLines =
                calculatePossibleLines(
                    paint,
                    invalidPossibles,
                    cellSize,
                    layoutDetails,
                    numeralSystem,
                )

            if (possiblesLines.size <= 2) {
                return possiblesLines
            }
        }

        paint.textSize = (averageLengthOfCell / 6.0f).toInt().toFloat()
        return calculatePossibleLines(paint, invalidPossibles, cellSize, layoutDetails, numeralSystem)
    }

    private fun calculatePossibleLines(
        paint: Paint,
        invalidPossibles: List<Int>,
        cellSize: Pair<Float, Float>,
        layoutDetails: GridLayoutDetails,
        numeralSystem: NumeralSystem,
    ): List<SpannableStringBuilder> {
        val averageLengthOfCell = (cellSize.first + cellSize.second) / 2

        if (averageLengthOfCell < 35) {
            return listOf(
                SpannableStringBuilder()
                    .append("..."),
            )
        }

        val possiblesLines = mutableListOf<MutableSet<Int>>()

        // adds all possible to one line
        var currentLine =
            cell.possibles
                .sorted()
                .toMutableSet()

        possiblesLines += currentLine
        var currentLineText = getPossiblesLineText(numeralSystem, currentLine, invalidPossibles)

        while (paint.measureText(currentLineText.toString()) > cellSize.first - 2 * layoutDetails.possibleNumbersMarginX()) {
            val newLine = mutableSetOf<Int>()
            possiblesLines += newLine
            while (paint.measureText(currentLineText.toString()) > cellSize.first - 2 * layoutDetails.possibleNumbersMarginX()) {
                val firstDigitOfCurrentLine = currentLine.first()
                newLine.add(firstDigitOfCurrentLine)
                currentLine.remove(firstDigitOfCurrentLine)
                currentLineText = getPossiblesLineText(numeralSystem, currentLine, invalidPossibles)
            }
            currentLine = newLine
            currentLineText = getPossiblesLineText(numeralSystem, currentLine, invalidPossibles)
        }

        return possiblesLines.map { getPossiblesLineText(numeralSystem, it, invalidPossibles) }
    }

    private fun drawPossibleNumbersWithFixedGrid(
        canvas: Canvas,
        variant: GameVariant,
        paint: Paint,
        invalidPossibles: List<Int>,
        layoutDetails: GridLayoutDetails,
        numeralSystem: NumeralSystem,
    ) {
        paint.textSize = (layoutDetails.averageLengthOfCell() / 4.75).toInt().toFloat()
        val xOffset = layoutDetails.possibleNumbersMarginX() * 2

        val yOffset =
            if (variant.possibleDigits.size <= 6) {
                layoutDetails.yOffsetUpToSixValues()
            } else {
                layoutDetails.yOffsetFromSevenOn()
            }

        val yOffsetPerRow =
            if (variant.possibleDigits.size <= 6) {
                layoutDetails.possiblesFixedGridDistanceYUpToSixValues()
            } else {
                layoutDetails.possiblesFixedGridDistanceYFromSevenValuesOn()
            }

        val digits: MutableList<Int?> = variant.possibleDigits.toMutableList()

        if (variant.options.digitSetting.zeroOnKeyPadShouldBePlacedAtLast()) {
            digits.remove(0)

            while ((digits.size.mod(3)) != 2) {
                digits.add(digits.size, null)
            }

            digits.add(digits.size, 0)
        }

        for (possible in cell.possibles) {
            val index = digits.indexOf(possible)

            val regularPaintColor = paint.color

            if (possible in invalidPossibles) {
                paint.color = paintHolder.colorInvalidPossible()
            }

            canvas.drawText(
                numeralSystem.displayableString(possible),
                cellUI.westPixel + xOffset + index % 3 * layoutDetails.possiblesFixedGridDistanceX(),
                cellUI.northPixel + yOffset + index / 3 * yOffsetPerRow,
                paint,
            )

            if (possible in invalidPossibles) {
                paint.color = regularPaintColor
            }
        }
    }

    private fun getPossiblesLineText(
        numeralSystem: NumeralSystem,
        possibles: Set<Int>,
        invalidPossibles: List<Int>,
    ): SpannableStringBuilder {
        val stringBuilder =
            SpannableStringBuilder()

        var first = true

        possibles.forEach { possible ->
            if (!first) {
                stringBuilder.append("|")
            }

            if (possible in invalidPossibles) {
                stringBuilder.color(paintHolder.colorInvalidPossible()) {
                    stringBuilder.append(numeralSystem.displayableString(possible))
                }
            } else {
                stringBuilder.append(numeralSystem.displayableString(possible))
            }

            first = false
        }

        return stringBuilder
    }
}
