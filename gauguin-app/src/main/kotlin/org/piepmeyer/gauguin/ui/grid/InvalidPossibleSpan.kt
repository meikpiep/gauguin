package org.piepmeyer.gauguin.ui.grid

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.text.style.ReplacementSpan
import kotlin.math.roundToInt

class InvalidPossibleSpan(
    private val textPaint: Paint,
    private val backgroundPaint: Paint,
    private val framePaint: Paint,
    private val cornerRadius: Float,
) : ReplacementSpan() {
    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint,
    ) {
        val originalColor = paint.color

        val textToDisplay = text.substring(start, end)
        val measuredWidth = paint.measureText(textToDisplay)
        val horizontalMargin = (paint.measureText(text, start, end) * 0.25f).roundToInt()
        val verticalMarginReduction = ((y - bottom).toFloat() / 2f)

        canvas.drawRoundRect(
            x - horizontalMargin,
            top.toFloat() - verticalMarginReduction,
            x + measuredWidth + horizontalMargin,
            bottom.toFloat() + verticalMarginReduction,
            cornerRadius,
            cornerRadius,
            backgroundPaint,
        )

        canvas.drawRoundRect(
            x - horizontalMargin,
            top.toFloat() - verticalMarginReduction,
            x + measuredWidth + horizontalMargin,
            bottom.toFloat() + verticalMarginReduction,
            cornerRadius,
            cornerRadius,
            framePaint,
        )

        paint.color = textPaint.color
        canvas.drawText(textToDisplay, x, y.toFloat(), paint)

        paint.color = originalColor
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: FontMetricsInt?,
    ): Int = paint.measureText(text, start, end).roundToInt()
}
