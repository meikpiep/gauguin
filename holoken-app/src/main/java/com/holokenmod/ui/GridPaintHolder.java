package com.holokenmod.ui;

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
	
	public GridPaintHolder(GridUI gridUI) {
		this.mBorderPaint = new Paint();
		this.mBorderPaint.setStrokeWidth(2);
		
		this.mCageSelectedPaint = new Paint();
		this.mCageSelectedPaint.setStrokeWidth(4);
		
		this.mUserSetPaint = new Paint();
		this.mWarningPaint = new Paint();
		this.mWarningTextPaint = new Paint();
		this.mCheatedPaint = new Paint();
		this.mSelectedPaint = new Paint();
		this.mLastModifiedPaint = new Paint();
		
		this.mCageTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.mCageTextPaint.setTextSize(14);
		
		this.mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		this.mPossiblesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.mPossiblesPaint.setTextSize(10);
		
		this.textOfSelectedCellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.textOfSelectedCellPaint.setTextSize(10);
		
		this.mSelectedPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorTertiary));
		this.mLastModifiedPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorSecondary));
		this.mLastModifiedPaint.setAlpha(220);
		
		this.textOfSelectedCellPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorOnSecondary));
		this.mPossiblesPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorOnBackground));
		
		this.mUserSetPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorSurface));
		
		this.mCageTextPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorPrimary));
		
		this.mValuePaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorOnBackground));
		this.mBorderPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorOnBackground));
		this.mCageSelectedPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorOnBackground));
		
		this.mWarningPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorError));
		this.mWarningTextPaint.setColor(MaterialColors.getColor(gridUI, R.attr.colorOnError));
		this.mCheatedPaint.setColor(MaterialColors.compositeARGBWithAlpha(MaterialColors.getColor(gridUI, R.attr.colorError), 200));
	}
}
