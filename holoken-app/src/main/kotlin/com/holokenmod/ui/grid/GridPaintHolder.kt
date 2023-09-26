package com.holokenmod.ui.grid

import android.graphics.Paint
import androidx.core.graphics.ColorUtils
import com.google.android.material.color.MaterialColors
import com.holokenmod.grid.GridBorderType
import com.holokenmod.grid.GridCage
import com.holokenmod.grid.GridCell

class GridPaintHolder(gridUI: GridUI) {
    private val valuePaint: Paint = Paint()
    private val borderPaint: Paint = Paint()
    private val cageSelectedPaint: Paint = Paint()
    private val cageTextPaint: Paint = Paint()
    private val cageTextPreviewModePaint: Paint = Paint()
    private val possiblesPaint: Paint = Paint()
    private val textOfSelectedCellPaint: Paint = Paint()
    private val warningPaint: Paint = Paint()
    private val warningTextPaint: Paint = Paint()
    private val cheatedPaint: Paint = Paint()
    private val selectedPaint: Paint = Paint()
    private val userSetPaint: Paint = Paint()
    private val lastModifiedPaint: Paint = Paint()

    init {
        borderPaint.strokeWidth = 2f
        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnBackground)

        cageSelectedPaint.strokeWidth = 4f
        cageSelectedPaint.style = Paint.Style.STROKE
        cageSelectedPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnBackground)

        cageTextPaint.textSize = 14f
        cageTextPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorPrimary)

        cageTextPreviewModePaint.textSize = 14f
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(cageTextPaint.color, hsl)
        hsl[1] = hsl[1] * 0.35f
        cageTextPreviewModePaint.color = ColorUtils.HSLToColor(hsl)

        valuePaint.flags = Paint.ANTI_ALIAS_FLAG
        valuePaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnBackground)

        possiblesPaint.flags = Paint.ANTI_ALIAS_FLAG
        possiblesPaint.textSize = 10f
        possiblesPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnBackground)

        textOfSelectedCellPaint.flags = Paint.ANTI_ALIAS_FLAG
        textOfSelectedCellPaint.textSize = 10f
        textOfSelectedCellPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnTertiaryContainer)

        selectedPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorTertiaryContainer)

        lastModifiedPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorTertiaryContainer)
        lastModifiedPaint.alpha = 120

        userSetPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorSurface)

        warningPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorErrorContainer)
        warningPaint.strokeWidth = 6f

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
            textOfSelectedCellPaint
        } else {
            possiblesPaint
        }
    }

    fun cellValuePaint(cell: GridCell) = if (cell.isSelected) {
        textOfSelectedCellPaint
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
            cage.getCell(0).isSelected -> textOfSelectedCellPaint
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

    fun previewBannerTextPaint(): Paint = textOfSelectedCellPaint

    fun previewBannerBackgroundPaint(): Paint = selectedPaint
}