package com.holokenmod.ui.grid;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

import com.holokenmod.Direction;
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
	private float mPosX;
	private float mPosY;
	
	GridCellUI(final GridUI grid, final GridCell cell, final GridPaintHolder paintHolder) {
		this.grid = grid;
		this.cell = cell;
		this.paintHolder = paintHolder;
		
		this.mPosX = 0;
		this.mPosY = 0;
	}
	
	@NonNull
	@Override
	public String toString() {
		return "<cell:" + this.cell.getCellNumber() + " col:" + this.cell.getColumn() +
				" row:" + this.cell.getRow() + " posX:" + this.mPosX + " posY:" +
				this.mPosY + " val:" + this.cell.getValue() + ", userval: " + this.cell
				.getUserValue() + ">";
	}
	
	private Paint getBorderPaint(final Direction border) {
		switch (this.cell.getCellBorders().getBorderType(border)) {
			case BORDER_NONE:
				return null;
			case BORDER_SOLID:
				return paintHolder.mBorderPaint;
			case BORDER_WARN:
				return paintHolder.mWarningPaint;
			case BORDER_CAGE_SELECTED:
				return paintHolder.mCageSelectedPaint;
		}
		return null;
	}
	
	void onDraw(final Canvas canvas, final boolean onlyBorders, final float cellSize) {
		this.mPosX = cellSize * this.cell.getColumn();
		this.mPosY = cellSize * this.cell.getRow();
		
		drawBorders(canvas, onlyBorders, cellSize);
		
		if (onlyBorders) {
			return;
		}
		
		drawCellValue(canvas, cellSize);
		drawCageText(canvas, cellSize);
		
		if (!cell.getPossibles().isEmpty()) {
			drawPossibleNumbers(canvas, cellSize);
		}
	}
	
	private void drawBorders(Canvas canvas, boolean onlyBorders, float cellSize) {
		float north = this.mPosY;
		float south = this.mPosY + cellSize;
		float east = this.mPosX + cellSize;
		float west = this.mPosX;
		
		final boolean cellAbove = this.grid.getGrid()
				.isValidCell(this.cell.getRow() - 1, this.cell.getColumn());
		final boolean cellLeft = this.grid.getGrid()
				.isValidCell(this.cell.getRow(), this.cell.getColumn() - 1);
		final boolean cellRight = this.grid.getGrid()
				.isValidCell(this.cell.getRow(), this.cell.getColumn() + 1);
		final boolean cellBelow = this.grid.getGrid()
				.isValidCell(this.cell.getRow() + 1, this.cell.getColumn());
		
		if (!onlyBorders) {
			drawCellBackground(canvas, north, south, east, west);
		} else {
			if (this.cell.getCellBorders().getBorderType(Direction.NORTH).isHighlighted()) {
				if (!cellAbove) {
					north += 2;
				} else {
					north += 1;
				}
			}
			if (this.cell.getCellBorders().getBorderType(Direction.WEST).isHighlighted()) {
				if (!cellLeft) {
					west += 2;
				} else {
					west += 1;
				}
			}
			if (this.cell.getCellBorders().getBorderType(Direction.EAST).isHighlighted()) {
				if (!cellRight) {
					east -= 3;
				} else {
					east -= 2;
				}
			}
			if (this.cell.getCellBorders().getBorderType(Direction.SOUTH).isHighlighted()) {
				if (!cellBelow) {
					south -= 3;
				} else {
					south -= 2;
				}
			}
		}
		
		// North
		Paint borderPaint = this.getBorderPaint(Direction.NORTH);
		if (!onlyBorders && this.cell.getCellBorders().getBorderType(Direction.NORTH)
				.isHighlighted()) {
			borderPaint = paintHolder.mBorderPaint;
		}
		if (borderPaint != null) {
			canvas.drawLine(west, north, east, north, borderPaint);
		}
		
		// East
		borderPaint = this.getBorderPaint(Direction.EAST);
		if (!onlyBorders && this.cell.getCellBorders().getBorderType(Direction.EAST)
				.isHighlighted()) {
			borderPaint = paintHolder.mBorderPaint;
		}
		if (borderPaint != null) {
			canvas.drawLine(east, north, east, south, borderPaint);
		}
		
		// South
		borderPaint = this.getBorderPaint(Direction.SOUTH);
		if (!onlyBorders && this.cell.getCellBorders().getBorderType(Direction.SOUTH)
				.isHighlighted()) {
			borderPaint = paintHolder.mBorderPaint;
		}
		if (borderPaint != null) {
			canvas.drawLine(west, south, east, south, borderPaint);
		}
		
		// West
		borderPaint = this.getBorderPaint(Direction.WEST);
		if (!onlyBorders && this.cell.getCellBorders().getBorderType(Direction.WEST)
				.isHighlighted()) {
			borderPaint = paintHolder.mBorderPaint;
		}
		if (borderPaint != null) {
			canvas.drawLine(west, north, west, south, borderPaint);
		}
	}
	
	private void drawCellBackground(Canvas canvas, float north, float south, float east, float west) {
		Paint paint = getCellBackgroundPaint();
		
		if (paint != null) {
			canvas.drawRect(west + 1, north + 1, east - 1, south - 1, paint);
		}
	}
	
	private Paint getCellBackgroundPaint() {
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
		
		if (this.cell.isSelected()) {
			return paintHolder.mSelectedPaint;
		}
		
		return null;
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
			
			canvas.drawText("" + this.cell.getUserValue(), this.mPosX + leftOffset,
					this.mPosY + topOffset, paint);
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
				this.mPosX + 2,
				this.mPosY + cageTextSize,
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
					mPosX + 3,
					mPosY + cellSize - 7 - lineHeigth * index,
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
			final float xPos = mPosX + xOffset + ((possible - 1) % 3) * xScale;
			final float yPos = mPosY + yOffset + ((possible - 1) / 3) * yScale;
			canvas.drawText(Integer.toString(possible), xPos, yPos, paint);
		}
	}
	
	private String getPossiblesLineText(SortedSet<Integer> possibles) {
		return StringUtils.join(possibles, "|");
	}
	
	GridCell getCell() {
		return this.cell;
	}
}