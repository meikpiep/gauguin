package org.piepmeyer.gauguin.ui.grid

import android.content.Context
import android.graphics.Paint
import androidx.core.graphics.ColorUtils
import com.google.android.material.color.MaterialColors
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell

class GridPaintHolder(
    gridUI: GridUI,
    private val context: Context,
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

    private val possiblesPaint: Paint = Paint()
    private val possiblesSelectedPaint: Paint = Paint()
    private val possiblesSelectedFastFinishModePaint: Paint = Paint()

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

        backgroundPaint.color = getColor(com.google.android.material.R.attr.colorSurface)
        backgroundPaint.style = Paint.Style.FILL

        borderPaint.strokeWidth = 2f
        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = getColor(com.google.android.material.R.attr.colorSecondary)

        gridPaint.flags = Paint.ANTI_ALIAS_FLAG
        gridPaint.color =
            ColorUtils.blendARGB(
                getColor(R.attr.colorGridCage),
                getColor(com.google.android.material.R.attr.colorSurface),
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
                getColor(com.google.android.material.R.attr.colorSurface),
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
        cageTextPaint.color = getColor(com.google.android.material.R.attr.colorPrimary)
        cageTextPaint.typeface = fontHolder.fontCageText

        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(cageTextPaint.color, hsl)
        hsl[1] = hsl[1] * 0.35f
        cageTextPreviewModePaint.flags = Paint.ANTI_ALIAS_FLAG
        cageTextPreviewModePaint.color = ColorUtils.HSLToColor(hsl)
        cageTextPreviewModePaint.typeface = fontHolder.fontCageText

        cageTextSelectedPaint.flags = Paint.ANTI_ALIAS_FLAG
        cageTextSelectedPaint.color = getColor(com.google.android.material.R.attr.colorPrimary)
        cageTextSelectedPaint.typeface = fontHolder.fontCageText
        cageTextSelectedFastFinishModePaint.flags = Paint.ANTI_ALIAS_FLAG
        cageTextSelectedFastFinishModePaint.color = getColor(com.google.android.material.R.attr.colorSurface)
        cageTextSelectedFastFinishModePaint.typeface = fontHolder.fontCageText

        valuePaint.flags = Paint.ANTI_ALIAS_FLAG
        valuePaint.color = getColor(com.google.android.material.R.attr.colorOnBackground)
        valuePaint.typeface = fontHolder.fontValue

        valueSelectedPaint.flags = Paint.ANTI_ALIAS_FLAG
        valueSelectedPaint.color = getColor(R.attr.colorGridSelected)
        valueSelectedPaint.typeface = fontHolder.fontValue
        valueSelectedFastFinishModePaint.flags = Paint.ANTI_ALIAS_FLAG
        valueSelectedFastFinishModePaint.color = getColor(com.google.android.material.R.attr.colorSurface)
        valueSelectedFastFinishModePaint.typeface = fontHolder.fontValue

        possiblesPaint.flags = Paint.ANTI_ALIAS_FLAG
        possiblesPaint.color =
            ColorUtils.setAlphaComponent(
                getColor(com.google.android.material.R.attr.colorOnBackground),
                225,
            )
        possiblesPaint.typeface = fontHolder.fontPossibles

        possiblesSelectedPaint.flags = Paint.ANTI_ALIAS_FLAG
        possiblesSelectedPaint.textSize = 6f
        possiblesSelectedPaint.color = getColor(R.attr.colorGridSelected)
        possiblesSelectedPaint.typeface = fontHolder.fontPossibles
        possiblesSelectedFastFinishModePaint.flags = Paint.ANTI_ALIAS_FLAG
        possiblesSelectedFastFinishModePaint.textSize = 6f
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
                getColor(com.google.android.material.R.attr.colorSurface),
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
}
