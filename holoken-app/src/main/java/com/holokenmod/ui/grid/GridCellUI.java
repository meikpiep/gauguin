package com.holokenmod.ui.grid;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

import com.holokenmod.grid.GridCell;
import com.holokenmod.options.ApplicationPreferences;

class GridCellUI {
	private final GridCell cell;
	private final GridUI grid;
	private final GridPaintHolder paintHolder;
	private float positionX;
	private float positionY;
	private final GridCellUIBorderDrawer borderDrawer;
	private final GridCellUIPossibleNumbersDrawer possibleNumbersDrawer;
	private float cellSize;
	
	GridCellUI(final GridUI grid, final GridCell cell, final GridPaintHolder paintHolder) {
		this.grid = grid;
		this.cell = cell;
		this.paintHolder = paintHolder;
		
		this.borderDrawer = new GridCellUIBorderDrawer(this, paintHolder);
		this.possibleNumbersDrawer = new GridCellUIPossibleNumbersDrawer(this, paintHolder);
		
		this.positionX = 0;
		this.positionY = 0;
	}
	
	float getPositionX() {
		return positionX;
	}
	
	float getPositionY() {
		return positionY;
	}
	
	@NonNull
	@Override
	public String toString() {
		return "<cell:" + this.cell.getCellNumber() + " col:" + this.cell.getColumn() +
				" row:" + this.cell.getRow() + " posX:" + this.positionX + " posY:" +
				this.positionY + " val:" + this.cell.getValue() + ", userval: " + this.cell
				.getUserValue() + ">";
	}
	
	void onDraw(final Canvas canvas, final float cellSize) {
		this.cellSize = cellSize;
		this.positionX = cellSize * this.cell.getColumn() + GridUI.BORDER_WIDTH;
		this.positionY = cellSize * this.cell.getRow() + GridUI.BORDER_WIDTH;
		
		drawCellBackground(canvas);
		
		borderDrawer.drawBorders(canvas);
		
		drawCellValue(canvas, cellSize);
		drawCageText(canvas, cellSize);
		
		if (!cell.getPossibles().isEmpty()) {
			possibleNumbersDrawer.drawPossibleNumbers(canvas, cellSize);
		}
	}
	
	private void drawCellValue(Canvas canvas, float cellSize) {
		if (this.cell.isUserValueSet()) {
			Paint paint;
			
			if (this.cell.isSelected()) {
				paint = paintHolder.textOfSelectedCellPaint;
			} else if (this.cell.isShowWarning() || this.cell.isCheated()) {
				paint = paintHolder.mWarningTextPaint;
			} else {
				paint = paintHolder.mValuePaint;
			}
			
			final int textSize = (int) (cellSize * 3 / 4);
			paint.setTextSize(textSize);
			
			final float leftOffset;
			
			if (this.cell.getUserValue() <= 9) {
				leftOffset = cellSize / 2 - textSize / 4;
			} else {
				leftOffset = cellSize / 2 - textSize / 2;
			}
			
			final float topOffset = cellSize / 2 + textSize * 2 / 5;
			
			canvas.drawText("" + this.cell.getUserValue(), this.positionX + leftOffset,
					this.positionY + topOffset, paint);
		}
	}
	
	private void drawCageText(Canvas canvas, float cellSize) {
		if (this.getCell().getCageText().isEmpty()) {
			return;
		}
		
		Paint paint;
		
		if (grid.isPreviewMode()) {
			float[] hsl = new float[3];
			
			ColorUtils.colorToHSL(paintHolder.mCageTextPaint.getColor(), hsl);
			hsl[1] = hsl[1] * 0.35f;
			
			paint = new Paint();
			paint.setColor(ColorUtils.HSLToColor(hsl));
		} else if (cell.isSelected() || cell.isLastModified()) {
			paint = paintHolder.textOfSelectedCellPaint;
		} else {
			paint = paintHolder.mCageTextPaint;
		}
		
		final int cageTextSize = (int) (cellSize / 3);
		paint.setTextSize(cageTextSize);
		
		canvas.drawText(
				this.getCell().getCageText(),
				this.positionX + 4,
				this.positionY + cageTextSize,
				paint);
	}
	
	GridCell getCell() {
		return this.cell;
	}
	
	private void drawCellBackground(Canvas canvas) {
		Paint paint = getCellBackgroundPaint();
		
		if (paint != null) {
			canvas.drawRect(getWestPixel() + 1,
					getNorthPixel() + 1,
					getEastPixel() - 1,
					getSouthPixel() - 1,
					paint);
		}
	}
	
	private Paint getCellBackgroundPaint() {
		if (this.cell.isSelected()) {
			return paintHolder.mSelectedPaint;
		}
		
		if (this.cell.isLastModified()) {
			return paintHolder.mLastModifiedPaint;
		}
		
		if (this.cell.isCheated()) {
			return paintHolder.mCheatedPaint;
		}
		
		if ((this.cell.isShowWarning() && ApplicationPreferences.getInstance()
				.showDupedDigits()) || this.cell.isInvalidHighlight()) {
			return paintHolder.mWarningPaint;
		}
		
		return null;
	}
	
	float getNorthPixel() {
		return positionY;
	}
	
	float getSouthPixel() {
		return positionY + cellSize;
	}
	
	float getEastPixel() {
		return positionX + cellSize;
	}
	
	float getWestPixel() {
		return positionX;
	}
}