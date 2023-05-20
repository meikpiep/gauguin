package com.holokenmod.ui.grid

import android.graphics.Paint
import com.google.android.material.color.MaterialColors

class GridPaintHolder(gridUI: GridUI) {
    val mValuePaint: Paint
    val mBorderPaint: Paint = Paint()
    val mCageSelectedPaint: Paint
    val mCageTextPaint: Paint
    val mPossiblesPaint: Paint
    val textOfSelectedCellPaint: Paint
    val mWarningPaint: Paint
    val mWarningTextPaint: Paint
    val mCheatedPaint: Paint
    val mSelectedPaint: Paint
    private val mUserSetPaint: Paint
    val mLastModifiedPaint: Paint

    init {
        mBorderPaint.strokeWidth = 2f
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnBackground)
        mCageSelectedPaint = Paint()
        mCageSelectedPaint.strokeWidth = 4f
        mCageSelectedPaint.style = Paint.Style.STROKE
        mCageSelectedPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnBackground)
        mCageTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mCageTextPaint.textSize = 14f
        mCageTextPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorPrimary)
        mValuePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mValuePaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnBackground)
        mPossiblesPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPossiblesPaint.textSize = 10f
        textOfSelectedCellPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textOfSelectedCellPaint.textSize = 10f
        mSelectedPaint = Paint()
        mSelectedPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorTertiary)
        mLastModifiedPaint = Paint()
        mLastModifiedPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorSecondary)
        mLastModifiedPaint.alpha = 170
        textOfSelectedCellPaint.color =
            MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnSecondary)
        mPossiblesPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorOnBackground)
        mUserSetPaint = Paint()
        mUserSetPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorSurface)
        mWarningPaint = Paint()
        mWarningPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorErrorContainer)
        mWarningPaint.strokeWidth = 6f
        mWarningTextPaint = Paint()
        mWarningTextPaint.color = MaterialColors.getColor(gridUI, com.google.android.material.R.attr.colorError)
        mCheatedPaint = Paint()
        mCheatedPaint.color = MaterialColors.compositeARGBWithAlpha(
            MaterialColors.getColor(
                gridUI,
                com.google.android.material.R.attr.colorErrorContainer
            ), 128
        )
    }
}