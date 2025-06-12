package org.piepmeyer.gauguin.ui.grid

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import androidx.core.graphics.ColorUtils
import com.google.android.material.color.MaterialColors
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell

class GridPaintHolder(
    gridUI: GridUI,
    private val context: Context,
    usePlainBlackBackground: Boolean? = false,
) {
    private val backgroundPaint: Paint = Paint()

    private val valuePaint: Paint = Paint()
    private val valueSelectedPaint: Paint = Paint()
    private val valueSelectedFastFinishModePaint: Paint = Paint()

    private val borderPaint: Paint = Paint()
    private val gridPaint: Paint = Paint()
    private val selectedGridPaint: Paint
    val warningGridPaint: Paint
    private val innerGridPaint: Paint = Paint()

    private val cageSelectedPaint: Paint = Paint()

    private val cageTextPaint: Paint = Paint()
    private val cageTextSelectedPaint: Paint = Paint()
    private val cageTextSelectedFastFinishModePaint: Paint = Paint()
    private val cageTextPreviewModePaint: Paint = Paint()

    private val possiblesPaint: TextPaint = TextPaint()
    private val possiblesSelectedPaint: TextPaint = TextPaint()
    private val possiblesSelectedFastFinishModePaint: TextPaint = TextPaint()

    private val warningTextPaint: Paint = Paint()
    private val cheatedPaint: Paint = Paint()
    private val errorBackgroundPaint: Paint = Paint()

    private val selectedPaint: Paint = Paint()
    private val selectedFastFinishModePaint: Paint = Paint()
    private val textOnSelectedFastFinishModePaint: Paint = Paint()
    private val lastModifiedPaint: Paint = Paint()

    private val previewPaint: Paint = Paint()
    private val previewTextPaint: Paint = Paint()

    init {
        val fontHolder = GridFontHolder(context)

        val surfaceColor =
            if (usePlainBlackBackground == true) {
                Color.BLACK
            } else {
                getColor(com.google.android.material.R.attr.colorSurface)
            }

        backgroundPaint.color = surfaceColor
        backgroundPaint.style = Paint.Style.FILL

        borderPaint.strokeWidth = 2f
        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = getColor(com.google.android.material.R.attr.colorSecondary)

        gridPaint.flags = Paint.ANTI_ALIAS_FLAG
        gridPaint.color =
            ColorUtils.blendARGB(
                getColor(R.attr.colorGridCage),
                surfaceColor,
                if (gridUI.isInEditMode) {
                    0.0f
                } else {
                    1.0f - gridUI.resources.getFraction(R.fraction.gradCageOpacity, 1, 1)
                },
            )
        gridPaint.strokeJoin = Paint.Join.ROUND
        gridPaint.style = Paint.Style.STROKE

        selectedGridPaint = Paint(gridPaint)
        selectedGridPaint.color =
            ColorUtils.blendARGB(
                getColor(com.google.android.material.R.attr.colorSecondary),
                surfaceColor,
                0.1f,
            )

        warningGridPaint = Paint(gridPaint)
        warningGridPaint.color = getColor(com.google.android.material.R.attr.colorError)

        innerGridPaint.flags = Paint.ANTI_ALIAS_FLAG
        innerGridPaint.color = gridPaint.color

        cageSelectedPaint.flags = Paint.ANTI_ALIAS_FLAG
        cageSelectedPaint.style = Paint.Style.STROKE
        cageSelectedPaint.color = getColor(com.google.android.material.R.attr.colorOnBackground)
        cageSelectedPaint.typeface = fontHolder.fontValue

        cageTextPaint.flags = Paint.ANTI_ALIAS_FLAG
        cageTextPaint.color = getColor(R.attr.colorGridCageText)
        cageTextPaint.typeface = fontHolder.fontCageText

        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(cageTextPaint.color, hsl)
        hsl[1] = hsl[1] * 0.35f
        cageTextPreviewModePaint.flags = Paint.ANTI_ALIAS_FLAG
        cageTextPreviewModePaint.color = ColorUtils.HSLToColor(hsl)
        cageTextPreviewModePaint.typeface = fontHolder.fontCageText

        cageTextSelectedPaint.flags = Paint.ANTI_ALIAS_FLAG
        cageTextSelectedPaint.color = getColor(R.attr.colorGridCageText)
        cageTextSelectedPaint.typeface = fontHolder.fontCageText
        cageTextSelectedFastFinishModePaint.flags = Paint.ANTI_ALIAS_FLAG
        cageTextSelectedFastFinishModePaint.color = surfaceColor
        cageTextSelectedFastFinishModePaint.typeface = fontHolder.fontCageText

        valuePaint.flags = Paint.ANTI_ALIAS_FLAG
        valuePaint.color = getColor(R.attr.colorGridValue)
        valuePaint.typeface = fontHolder.fontValue

        valueSelectedPaint.flags = Paint.ANTI_ALIAS_FLAG
        valueSelectedPaint.color = getColor(R.attr.colorGridSelected)
        valueSelectedPaint.typeface = fontHolder.fontValue
        valueSelectedFastFinishModePaint.flags = Paint.ANTI_ALIAS_FLAG
        valueSelectedFastFinishModePaint.color = surfaceColor
        valueSelectedFastFinishModePaint.typeface = fontHolder.fontValue

        possiblesPaint.flags = Paint.ANTI_ALIAS_FLAG
        possiblesPaint.color =
            ColorUtils.setAlphaComponent(
                getColor(com.google.android.material.R.attr.colorOnSurface),
                225,
            )
        possiblesPaint.typeface = fontHolder.fontPossibles
        possiblesSelectedPaint.flags = Paint.ANTI_ALIAS_FLAG
        possiblesSelectedPaint.color = getColor(R.attr.colorGridSelected)
        possiblesSelectedPaint.typeface = fontHolder.fontPossibles
        possiblesSelectedFastFinishModePaint.flags = Paint.ANTI_ALIAS_FLAG
        possiblesSelectedFastFinishModePaint.color = getColor(R.attr.colorGridSelectedText)
        possiblesSelectedFastFinishModePaint.typeface = fontHolder.fontPossibles

        previewTextPaint.flags = Paint.ANTI_ALIAS_FLAG
        previewTextPaint.textSize = 6f
        previewTextPaint.color = getColor(com.google.android.material.R.attr.colorOnTertiaryContainer)
        previewTextPaint.typeface = fontHolder.fontPossibles

        previewPaint.color = getColor(com.google.android.material.R.attr.colorTertiaryContainer)

        selectedPaint.flags = Paint.ANTI_ALIAS_FLAG
        selectedPaint.color = getColor(R.attr.colorGridSelected)
        selectedPaint.style = Paint.Style.STROKE

        selectedFastFinishModePaint.flags = Paint.ANTI_ALIAS_FLAG
        selectedFastFinishModePaint.color = getColor(R.attr.colorGridSelected)
        selectedFastFinishModePaint.style = Paint.Style.FILL_AND_STROKE
        textOnSelectedFastFinishModePaint.flags = Paint.ANTI_ALIAS_FLAG
        textOnSelectedFastFinishModePaint.color = getColor(R.attr.colorGridSelectedText)

        lastModifiedPaint.color =
            ColorUtils.blendARGB(
                getColor(R.attr.colorGridSelected),
                surfaceColor,
                0.5f,
            )
        lastModifiedPaint.style = Paint.Style.STROKE
        lastModifiedPaint.flags = Paint.ANTI_ALIAS_FLAG

        warningTextPaint.color = getColor(com.google.android.material.R.attr.colorError)
        warningTextPaint.typeface = fontHolder.fontValue

        cheatedPaint.color = getColor(com.google.android.material.R.attr.colorSurfaceVariant)

        errorBackgroundPaint.color =
            MaterialColors.compositeARGBWithAlpha(
                MaterialColors.getColor(
                    gridUI,
                    com.google.android.material.R.attr.colorErrorContainer,
                ),
                128,
            )
    }

    fun possiblesPaint(
        cell: GridCell,
        fastFinishMode: Boolean,
    ) = when {
        cell.isSelected && fastFinishMode -> possiblesSelectedFastFinishModePaint
        cell.isSelected -> possiblesSelectedPaint
        else -> possiblesPaint
    }

    fun cellValuePaint(
        cell: GridCell,
        fastFinishMode: Boolean,
    ) = when {
        cell.isInvalidHighlight -> warningTextPaint
        cell.isSelected && fastFinishMode -> textOnSelectedFastFinishModePaint
        cell.isSelected -> valueSelectedPaint
        else -> valuePaint
    }

    fun cellBackgroundPaint(
        cell: GridCell,
        badMathInCage: Boolean,
        markDuplicatedInRowOrColumn: Boolean,
        fastFinishMode: Boolean,
    ) = when {
        cell.isSelected && fastFinishMode -> selectedFastFinishModePaint
        cell.isCheated -> cheatedPaint
        (markDuplicatedInRowOrColumn && cell.duplicatedInRowOrColumn) || badMathInCage || cell.isInvalidHighlight -> errorBackgroundPaint
        else -> null
    }

    fun cellForegroundPaint(cell: GridCell) =
        when {
            cell.isSelected -> selectedPaint
            cell.isLastModified -> lastModifiedPaint
            else -> null
        }

    fun cageTextPaint(
        cage: GridCage,
        previewMode: Boolean,
        fastFinishMode: Boolean,
    ): Paint =
        when {
            previewMode -> cageTextPreviewModePaint
            cage.getCell(0).isSelected && fastFinishMode -> cageTextSelectedFastFinishModePaint
            cage.getCell(0).isSelected -> cageTextSelectedPaint
            else -> cageTextPaint
        }

    fun previewBannerTextPaint(): Paint = previewTextPaint

    fun previewBannerBackgroundPaint(): Paint = previewPaint

    fun backgroundPaint(): Paint = backgroundPaint

    fun innerGridPaint(): Paint = innerGridPaint

    fun gridPaint(): Paint = gridPaint

    fun selectedGridPaint(): Paint = selectedGridPaint

    private fun getColor(colorId: Int): Int = MaterialColors.getColor(context, colorId, "ups")

    fun colorInvalidPossible(): Int = getColor(R.attr.colorGridWarningText)
}
