package com.holokenmod.ui.grid

import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import com.google.android.material.color.MaterialColors
import com.holokenmod.R
import com.holokenmod.grid.GridBorderType
import com.holokenmod.grid.GridCage
import com.holokenmod.grid.GridCell

class GridPaintHolder(gridUI: GridUI) {
    private val valuePaint: Paint = Paint()
    private val valueSelectedPaint: Paint = Paint()

    private val borderPaint: Paint = Paint()

    private val cageSelectedPaint: Paint = Paint()

    private val cageTextPaint: Paint = Paint()
    private val cageTextSelectedPaint: Paint = Paint()
    private val cageTextPreviewModePaint: Paint = Paint()

    private val possiblesPaint: Paint = Paint()
    private val possiblesSelectedPaint: Paint = Paint()

    private val warningPaint: Paint = Paint()
    private val warningTextPaint: Paint = Paint()
    private val cheatedPaint: Paint = Paint()

    private val selectedPaint: Paint = Paint()
    private val userSetPaint: Paint = Paint()
    private val lastModifiedPaint: Paint = Paint()

    private val previewPaint: Paint = Paint()
    private val previewTextPaint: Paint = Paint()

    init {
        val fontPossibles = ResourcesCompat.getFont(gridUI.context, R.font.lato_regular)
        val fontRegular = ResourcesCompat.getFont(gridUI.context, R.font.lato_regular)
        val fontCageText = ResourcesCompat.getFont(gridUI.context, R.font.lato_bold)
        val fontValue = ResourcesCompat.getFont(gridUI.context, R.font.lato_regular)

        borderPaint.strokeWidth = 2f
        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorSecondary)

        cageSelectedPaint.style = Paint.Style.STROKE
        cageSelectedPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnBackground)
        cageSelectedPaint.typeface = fontRegular

        cageTextPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorPrimary)
        cageTextPaint.typeface = fontCageText

        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(cageTextPaint.color, hsl)
        hsl[1] = hsl[1] * 0.35f
        cageTextPreviewModePaint.color = ColorUtils.HSLToColor(hsl)
        cageTextPreviewModePaint.typeface = fontCageText

        cageTextSelectedPaint.flags = Paint.ANTI_ALIAS_FLAG
        cageTextSelectedPaint.color = MaterialColors.getColor(gridUI, R.attr.colorOnCustomColor1)
        cageTextSelectedPaint.typeface = fontCageText

        valuePaint.flags = Paint.ANTI_ALIAS_FLAG
        valuePaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnBackground)
        valuePaint.typeface = fontValue

        valueSelectedPaint.flags = Paint.ANTI_ALIAS_FLAG
        valueSelectedPaint.color = MaterialColors.getColor(gridUI, R.attr.colorOnCustomColor1)
        valueSelectedPaint.typeface = fontValue

        possiblesPaint.flags = Paint.ANTI_ALIAS_FLAG
        possiblesPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnBackground)
        possiblesPaint.typeface = fontPossibles

        possiblesSelectedPaint.flags = Paint.ANTI_ALIAS_FLAG
        possiblesSelectedPaint.textSize = 6f
        possiblesSelectedPaint.color = MaterialColors.getColor(gridUI, R.attr.colorOnCustomColor1)
        possiblesSelectedPaint.typeface = fontPossibles

        previewTextPaint.flags = Paint.ANTI_ALIAS_FLAG
        previewTextPaint.textSize = 6f
        previewTextPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnTertiaryContainer)
        previewTextPaint.typeface = fontPossibles

        previewPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorTertiaryContainer)

        selectedPaint.color = MaterialColors.getColor(gridUI, R.attr.colorCustomColor1)

        lastModifiedPaint.color = MaterialColors.getColor(gridUI, R.attr.colorCustomColor1)
        lastModifiedPaint.alpha = 120

        userSetPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorSurface)

        warningPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorErrorContainer)
        warningPaint.strokeWidth = 6f
        warningPaint.typeface = fontRegular

        warningTextPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorError)

        cheatedPaint.color = MaterialColors.compositeARGBWithAlpha(
            MaterialColors.getColor(
                gridUI,
                com.google.android.material.R.attr.colorErrorContainer
            ), 128
        )
    }

    fun possiblesPaint(cell: GridCell): Paint {
        return if (cell.isSelected) {
            possiblesSelectedPaint
        } else {
            possiblesPaint
        }
    }

    fun cellValuePaint(cell: GridCell) = if (cell.isSelected) {
        valueSelectedPaint
    } else if (cell.duplicatedInRowOrColumn || cell.isCheated) {
        warningTextPaint
    } else {
        valuePaint
    }

    fun cellBackgroundPaint(cell: GridCell) = when {
        cell.isSelected -> selectedPaint
        cell.isLastModified -> lastModifiedPaint
        cell.isCheated -> cheatedPaint
        cell.isInvalidHighlight -> warningPaint
        else -> null
    }

    fun cageTextPaint(cage: GridCage, previewMode: Boolean): Paint {
        return when {
            previewMode -> cageTextPreviewModePaint
            cage.getCell(0).isSelected -> cageTextSelectedPaint
            else -> cageTextPaint
        }
    }


    fun getBorderPaint(border: GridBorderType): Paint? {
        return when (border) {
            GridBorderType.BORDER_NONE -> null
            GridBorderType.BORDER_SOLID -> borderPaint
            GridBorderType.BORDER_WARN -> warningPaint
            GridBorderType.BORDER_CAGE_SELECTED -> cageSelectedPaint
            else -> null
        }
    }

    fun previewBannerTextPaint(): Paint = previewTextPaint

    fun previewBannerBackgroundPaint(): Paint = previewPaint
}