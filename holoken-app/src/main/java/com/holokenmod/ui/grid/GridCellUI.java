package com.holokenmod.ui.grid;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

import com.holokenmod.grid.GridCell;
import com.holokenmod.options.ApplicationPreferences;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

class GridCellUI {
	private final GridCell cell;
	private final GridUI grid;
	private final GridPaintHolder paintHolder;
	private float positionX;
	private float positionY;
	private GridCellUIBorderDrawer borderDrawer;
	private float cellSize;
	
	GridCellUI(final GridUI grid, final GridCell cell, final GridPaintHolder paintHolder) {
		this.grid = grid;
		this.cell = cell;
		this.paintHolder = paintHolder;
		
		this.borderDrawer = new GridCellUIBorderDrawer(this, paintHolder);
		
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
	
	void onDraw(final Canvas canvas, final boolean onlyBorders, final float cellSize) {
		this.cellSize = cellSize;
		this.positionX = cellSize * this.cell.getColumn() + GridUI.BORDER_WIDTH;
		this.positionY = cellSize * this.cell.getRow() + GridUI.BORDER_WIDTH;
		
		if (!onlyBorders) {
			drawCellBackground(canvas);
		}
		
		borderDrawer.drawBorders(canvas, onlyBorders);
		
		if (onlyBorders) {
			return;
		}
		
		drawCellValue(canvas, cellSize);
		drawCageText(canvas, cellSize);
		
		if (!cell.getPossibles().isEmpty()) {
			drawPossibleNumbers(canvas, cellSize);
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
				this.positionX + 2,
				this.positionY + cageTextSize,
				paint);
	}
	
	private void drawPossibleNumbers(Canvas canvas, float cellSize) {
		Paint possiblesPaint;
		
		if (this.cell.isSelected() || this.cell.isLastModified()) {
			possiblesPaint = paintHolder.textOfSelectedCellPaint;
		} else {
			possiblesPaint = paintHolder.mPossiblesPaint;
		}
		
		if (ApplicationPreferences.getInstance().show3x3Pencils()) {
			drawPossibleNumbersWithFixedGrid(canvas, cellSize, possiblesPaint);
		} else {
			drawPossibleNumbersDynamically(canvas, cellSize, possiblesPaint);
		}
	}
	
	private void drawPossibleNumbersDynamically(Canvas canvas, float cellSize, Paint paint) {
		paint.setFakeBoldText(false);
		paint.setTextSize((int) (cellSize / 4));
		
		List<SortedSet<Integer>> possiblesLines = new ArrayList<>();
		
		//adds all possible to one line
		TreeSet<Integer> currentLine = new TreeSet<>(cell.getPossibles());
		possiblesLines.add(currentLine);
		
		String currentLineText = getPossiblesLineText(currentLine);
		
		while (paint.measureText(currentLineText) > cellSize - 6) {
			TreeSet<Integer> newLine = new TreeSet<>();
			possiblesLines.add(newLine);
			
			while (paint.measureText(currentLineText) > cellSize - 6) {
				int firstDigitOfCurrentLine = currentLine.first();
				
				newLine.add(firstDigitOfCurrentLine);
				currentLine.remove(firstDigitOfCurrentLine);
				
				currentLineText = getPossiblesLineText(currentLine);
			}
			
			currentLine = newLine;
			currentLineText = getPossiblesLineText(currentLine);
		}
		
		int index = 0;
		
		Paint.FontMetricsInt metrics = paint.getFontMetricsInt();
		
		int lineHeigth = -metrics.ascent + metrics.leading + metrics.descent;
		
		for(SortedSet<Integer> possibleLine : possiblesLines) {
			canvas.drawText(getPossiblesLineText(possibleLine),
					positionX + 3,
					positionY + cellSize - 7 - lineHeigth * index,
					paint);
			
			index++;
		}
	}
	
	private void drawPossibleNumbersWithFixedGrid(Canvas canvas, float cellSize, Paint paint) {
		paint.setFakeBoldText(true);
		paint.setTextSize((int) (cellSize / 4.5));
		
		final int xOffset = (int) (cellSize / 3);
		final int yOffset = (int) (cellSize / 2) + 1;
		final float xScale = (float) 0.21 * cellSize;
		final float yScale = (float) 0.21 * cellSize;
		
		for (final int possible : cell.getPossibles()) {
			final float xPos = positionX + xOffset + ((possible - 1) % 3) * xScale;
			final float yPos = positionY + yOffset + ((possible - 1) / 3) * yScale;
			canvas.drawText(Integer.toString(possible), xPos, yPos, paint);
		}
	}
	
	private String getPossiblesLineText(SortedSet<Integer> possibles) {
		return StringUtils.join(possibles, "|");
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
	
	public float getNorthPixel() {
		return positionY;
	}
	
	public float getSouthPixel() {
		return positionY + cellSize;
	}
	
	public float getEastPixel() {
		return positionX + cellSize;
	}
	
	public float getWestPixel() {
		return positionX;
	}
}