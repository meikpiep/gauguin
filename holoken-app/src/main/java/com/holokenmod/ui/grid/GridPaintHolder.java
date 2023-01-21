package com.holokenmod.ui.grid;

import android.graphics.Paint;

import com.google.android.material.color.MaterialColors;
import com.holokenmod.R;

class GridPaintHolder {
	
	final Paint mValuePaint;
	final Paint mBorderPaint;
	final Paint mCageSelectedPaint;
	final Paint mCageTextPaint;
	final Paint mPossiblesPaint;
	final Paint textOfSelectedCellPaint;
	final Paint mWarningPaint;
	final Paint mWarningTextPaint;
	final Paint mCheatedPaint;
	final Paint mSelectedPaint;
	final Paint mUserSetPaint;
	final Paint mLastModifiedPaint;
	
	GridPaintHolder(GridUI gridUI) {
		this.mBorderPaint = new Paint();
		this.mBorderPaint.setStrokeWidth(2);
		this.mBorderPaint.setStyle(Paint.Style.STROKE);
		this.mBorderPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorOnBackground));
		
		this.mCageSelectedPaint = new Paint();
		this.mCageSelectedPaint.setStrokeWidth(4);
		this.mCageSelectedPaint.setStyle(Paint.Style.STROKE);
		this.mCageSelectedPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorOnBackground));
		
		this.mCageTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.mCageTextPaint.setTextSize(14);
		this.mCageTextPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorPrimary));
		
		this.mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.mValuePaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorOnBackground));
		
		this.mPossiblesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.mPossiblesPaint.setTextSize(10);
		
		this.textOfSelectedCellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.textOfSelectedCellPaint.setTextSize(10);
		
		this.mSelectedPaint = new Paint();
		this.mSelectedPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorTertiary));
		
		this.mLastModifiedPaint = new Paint();
		this.mLastModifiedPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorSecondary));
		this.mLastModifiedPaint.setAlpha(170);
		
		this.textOfSelectedCellPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorOnSecondary));
		this.mPossiblesPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorOnBackground));
		
		this.mUserSetPaint = new Paint();
		this.mUserSetPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorSurface));
		
		this.mWarningPaint = new Paint();
		this.mWarningPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorErrorContainer));
		this.mWarningPaint.setStrokeWidth(6);
		
		this.mWarningTextPaint = new Paint();
		this.mWarningTextPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorError));
		
		this.mCheatedPaint = new Paint();
		this.mCheatedPaint.setColor(MaterialColors.compositeARGBWithAlpha(MaterialColors.getColor(gridUI, R.attr.colorErrorContainer), 128));
	}
}
