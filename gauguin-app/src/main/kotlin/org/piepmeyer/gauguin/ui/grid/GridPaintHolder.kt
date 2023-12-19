package org.piepmeyer.gauguin.ui.grid

import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import com.google.android.material.color.MaterialColors
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCell

class GridPaintHolder(gridUI: GridUI) {
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

    private val selectedPaint: Paint = Paint()
    private val selectedFastFinishModePaint: Paint = Paint()
    private val textOnSelectedFastFinishModePaint: Paint = Paint()
    private val lastModifiedPaint: Paint = Paint()

    private val previewPaint: Paint = Paint()
    private val previewTextPaint: Paint = Paint()

    init {
        val fontPossibles = ResourcesCompat.getFont(gridUI.context, R.font.lato_regular)
        val fontRegular = ResourcesCompat.getFont(gridUI.context, R.font.lato_regular)
        val fontCageText = ResourcesCompat.getFont(gridUI.context, R.font.lato_bold)
        val fontValue = ResourcesCompat.getFont(gridUI.context, R.font.lato_regular)

        backgroundPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorSurface)
        backgroundPaint.style = Paint.Style.FILL

        borderPaint.strokeWidth = 2f
        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorSecondary)

        gridPaint.flags = Paint.ANTI_ALIAS_FLAG
        gridPaint.color = ColorUtils.blendARGB(
            gridUI.resources.getColor(R.color.gridCage, null),
            MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorSurface),
            1.0f - gridUI.resources.getFraction(R.fraction.gradCageOpacity, 1, 1)
        )
        gridPaint.strokeJoin = Paint.Join.ROUND
        gridPaint.style = Paint.Style.STROKE

        selectedGridPaint = Paint(gridPaint)
        selectedGridPaint.color = ColorUtils.blendARGB(
            MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorSecondary),
            MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorSurface),
            0.1f
        )

        warningGridPaint = Paint(gridPaint)
        warningGridPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorError)

        innerGridPaint.flags = Paint.ANTI_ALIAS_FLAG
        innerGridPaint.color = gridPaint.color

        cageSelectedPaint.flags = Paint.ANTI_ALIAS_FLAG
        cageSelectedPaint.style = Paint.Style.STROKE
        cageSelectedPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnBackground)
        cageSelectedPaint.typeface = fontRegular

        cageTextPaint.flags = Paint.ANTI_ALIAS_FLAG
        cageTextPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorPrimary)
        cageTextPaint.typeface = fontCageText

        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(cageTextPaint.color, hsl)
        hsl[1] = hsl[1] * 0.35f
        cageTextPreviewModePaint.flags = Paint.ANTI_ALIAS_FLAG
        cageTextPreviewModePaint.color = ColorUtils.HSLToColor(hsl)
        cageTextPreviewModePaint.typeface = fontCageText

        cageTextSelectedPaint.flags = Paint.ANTI_ALIAS_FLAG
        cageTextSelectedPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorPrimary)
        cageTextSelectedPaint.typeface = fontCageText
        cageTextSelectedFastFinishModePaint.flags = Paint.ANTI_ALIAS_FLAG
        cageTextSelectedFastFinishModePaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorSurface)
        cageTextSelectedFastFinishModePaint.typeface = fontCageText


        valuePaint.flags = Paint.ANTI_ALIAS_FLAG
        valuePaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnBackground)
        valuePaint.typeface = fontValue

        valueSelectedPaint.flags = Paint.ANTI_ALIAS_FLAG
        valueSelectedPaint.color = gridUI.resources.getColor(R.color.gridSelected, null)
        valueSelectedPaint.typeface = fontValue
        valueSelectedFastFinishModePaint.flags = Paint.ANTI_ALIAS_FLAG
        valueSelectedFastFinishModePaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorSurface)
        valueSelectedFastFinishModePaint.typeface = fontValue

        possiblesPaint.flags = Paint.ANTI_ALIAS_FLAG
        possiblesPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnBackground)
        possiblesPaint.typeface = fontPossibles

        possiblesSelectedPaint.flags = Paint.ANTI_ALIAS_FLAG
        possiblesSelectedPaint.textSize = 6f
        possiblesSelectedPaint.color = gridUI.resources.getColor(R.color.gridSelected, null)
        possiblesSelectedPaint.typeface = fontPossibles
        possiblesSelectedFastFinishModePaint.flags = Paint.ANTI_ALIAS_FLAG
        possiblesSelectedFastFinishModePaint.textSize = 6f
        possiblesSelectedFastFinishModePaint.color = gridUI.resources.getColor(R.color.gridSelectedText, null)
        possiblesSelectedFastFinishModePaint.typeface = fontPossibles

        previewTextPaint.flags = Paint.ANTI_ALIAS_FLAG
        previewTextPaint.textSize = 6f
        previewTextPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnTertiaryContainer)
        previewTextPaint.typeface = fontPossibles

        previewPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorTertiaryContainer)

        selectedPaint.flags = Paint.ANTI_ALIAS_FLAG
        selectedPaint.color = gridUI.resources.getColor(R.color.gridSelected, null)
        selectedPaint.style = Paint.Style.STROKE
        selectedFastFinishModePaint.flags = Paint.ANTI_ALIAS_FLAG
        selectedFastFinishModePaint.color = gridUI.resources.getColor(R.color.gridSelected, null)
        selectedFastFinishModePaint.style = Paint.Style.FILL_AND_STROKE
        textOnSelectedFastFinishModePaint.flags = Paint.ANTI_ALIAS_FLAG
        textOnSelectedFastFinishModePaint.color = gridUI.resources.getColor(R.color.gridSelectedText, null)

        lastModifiedPaint.color = ColorUtils.blendARGB(
            gridUI.resources.getColor(R.color.gridSelected, null),
            MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorSurface),
            0.5f
        )
        lastModifiedPaint.style = Paint.Style.STROKE
        lastModifiedPaint.flags = Paint.ANTI_ALIAS_FLAG

        warningTextPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorError)
        warningTextPaint.typeface = fontRegular

        cheatedPaint.color = MaterialColors.compositeARGBWithAlpha(
            MaterialColors.getColor(
                gridUI,
                com.google.android.material.R.attr.colorErrorContainer
            ), 128
        )
    }

    fun possiblesPaint(cell: GridCell, fastFinishMode: Boolean): Paint {
        return if (cell.isSelected) {
            if (fastFinishMode) { possiblesSelectedFastFinishModePaint } else { possiblesSelectedPaint }
        } else {
            possiblesPaint
        }
    }

    fun cellValuePaint(cell: GridCell, fastFinishMode: Boolean) = if (cell.isSelected) {
        if (fastFinishMode) { valueSelectedFastFinishModePaint } else { valueSelectedPaint }
    } else if (cell.duplicatedInRowOrColumn || cell.isCheated) {
        warningTextPaint
    } else {
        valuePaint
    }

    fun cellBackgroundPaint(cell: GridCell, fastFinishMode: Boolean) = when {
        cell.isSelected -> if (fastFinishMode) selectedFastFinishModePaint else selectedPaint
        cell.isLastModified -> lastModifiedPaint
        cell.isCheated -> cheatedPaint
        else -> null
    }

    fun cageTextPaint(cage: GridCage, previewMode: Boolean, fastFinishMode: Boolean): Paint {
        return when {
            previewMode -> cageTextPreviewModePaint
            cage.getCell(0).isSelected -> if (fastFinishMode) cageTextSelectedFastFinishModePaint else cageTextSelectedPaint
            else -> cageTextPaint
        }
    }

    fun previewBannerTextPaint(): Paint = previewTextPaint
    fun previewBannerBackgroundPaint(): Paint = previewPaint
    fun backgroundPaint(): Paint = backgroundPaint
    fun innerGridPaint(): Paint = innerGridPaint
    fun gridPaint(): Paint = gridPaint
    fun selectedGridPaint(): Paint = selectedGridPaint
}