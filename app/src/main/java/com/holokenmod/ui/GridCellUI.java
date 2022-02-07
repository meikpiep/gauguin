package com.holokenmod.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.holokenmod.Direction;
import com.holokenmod.Grid;
import com.holokenmod.GridBorderType;
import com.holokenmod.GridCell;
import com.holokenmod.GridCellBorders;
import com.holokenmod.Theme;
import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.options.GameVariant;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class GridCellUI {
	private static final String HAIR_SPACE = "\u200A";
	
	private final GridCell cell;
	private final Grid grid;
	private final Paint mValuePaint;
	private final Paint mBorderPaint;
	private final Paint mCageSelectedPaint;
	private final Paint mWrongBorderPaint;
	private final Paint mCageTextPaint;
	private final Paint mPossiblesPaint;
	private final Paint mWarningPaint;
	private final Paint mCheatedPaint;
	private final Paint mSelectedPaint;
	private final Paint mUserSetPaint;
	private final Paint mLastModifiedPaint;
	private final Paint flakyPaint;
	private float mPosX;
	private float mPosY;
	
	public GridCellUI(final Grid grid, final GridCell cell) {
		this.grid = grid;
		
		this.cell = cell;
		
		this.mPosX = 0;
		this.mPosY = 0;
		
		this.mBorderPaint = new Paint();
		this.mBorderPaint.setColor(0xFF000000);
		this.mBorderPaint.setStrokeWidth(2);
		
		this.mCageSelectedPaint = new Paint();
		this.mCageSelectedPaint.setColor(0xFF000000);
		this.mCageSelectedPaint.setStrokeWidth(4);
		
		this.mWrongBorderPaint = new Paint();
		this.mWrongBorderPaint.setColor(0xFFcc0000);
		this.mWrongBorderPaint.setStrokeWidth(3);
		
		this.mUserSetPaint = new Paint();
		this.mWarningPaint = new Paint();
		this.mCheatedPaint = new Paint();
		this.mSelectedPaint = new Paint();
		this.mLastModifiedPaint = new Paint();
		this.flakyPaint = new Paint();
		
		this.mUserSetPaint.setColor(0xFFFFFFFF);  //white
		this.mWarningPaint.setColor(0x90ff4444);  //red
		this.mCheatedPaint.setColor(0x99d6b4e6);  //purple
		this.mSelectedPaint.setColor(Color.rgb(105, 105, 105));
		this.mLastModifiedPaint.setColor(0x44eeff33); //yellow
		this.flakyPaint.setColor(Color.rgb(217, 144, 43)); //orange
		
		this.mCageTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.mCageTextPaint.setColor(0xFF33b5e5);
		this.mCageTextPaint.setTextSize(14);
		
		this.mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.mValuePaint.setColor(0xFF000000);
		
		this.mPossiblesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.mPossiblesPaint.setColor(0xFF000000);
		this.mPossiblesPaint.setTextSize(10);
		
		this.setBorders(GridBorderType.BORDER_NONE,
				GridBorderType.BORDER_NONE,
				GridBorderType.BORDER_NONE,
				GridBorderType.BORDER_NONE);
	}
	
	public void setTheme(final Theme theme) {
		if (theme == Theme.LIGHT) {
			this.mUserSetPaint.setColor(0xFFFFFFFF);
			this.mBorderPaint.setColor(0xFF000000);
			this.mCageSelectedPaint.setColor(0xFF000000);
			this.mValuePaint.setColor(0xFF000000);
			this.mPossiblesPaint.setColor(0xFF000000);
			this.mCageTextPaint.setColor(0xFF0086B3);
		} else if (theme == Theme.DARK) {
			this.mUserSetPaint.setColor(0xFF000000);
			this.mBorderPaint.setColor(0xFFFFFFFF);
			this.mCageSelectedPaint.setColor(0xFFFFFFFF);
			this.mValuePaint.setColor(0xFFFFFFFF);
			this.mPossiblesPaint.setColor(0xFFFFFFFF);
			this.mCageTextPaint.setColor(0xFF33b5e5);
		}
	}
	
	@NonNull
	@Override
	public String toString() {
		return "<cell:" + this.cell.getCellNumber() + " col:" + this.cell.getColumn() +
				" row:" + this.cell.getRow() + " posX:" + this.mPosX + " posY:" +
				this.mPosY + " val:" + this.cell.getValue() + ", userval: " + this.cell
				.getUserValue() + ">";
	}
	
	public void setBorders(final GridBorderType north, final GridBorderType east, final GridBorderType south, final GridBorderType west) {
		this.cell.setCellBorders(new GridCellBorders(north, east, south, west));
	}
	
	private Paint getBorderPaint(final Direction border) {
		switch (this.cell.getCellBorders().getBorderType(border)) {
			case BORDER_NONE:
				return null;
			case BORDER_SOLID:
				return this.mBorderPaint;
			case BORDER_WARN:
				return this.mWrongBorderPaint;
			case BORDER_CAGE_SELECTED:
				return this.mCageSelectedPaint;
		}
		return null;
	}
	
	public void onDraw(final Canvas canvas, final boolean onlyBorders, final float cellSize) {
		
		this.mPosX = cellSize * this.cell.getColumn();
		this.mPosY = cellSize * this.cell.getRow();
		
		float north = this.mPosY;
		float south = this.mPosY + cellSize;
		float east = this.mPosX + cellSize;
		float west = this.mPosX;
		final boolean cellAbove = this.grid
				.isValidCell(this.cell.getRow() - 1, this.cell.getColumn());
		final boolean cellLeft = this.grid
				.isValidCell(this.cell.getRow(), this.cell.getColumn() - 1);
		final boolean cellRight = this.grid
				.isValidCell(this.cell.getRow(), this.cell.getColumn() + 1);
		final boolean cellBelow = this.grid
				.isValidCell(this.cell.getRow() + 1, this.cell.getColumn());
		
		if (!onlyBorders) {
			if (this.cell.isUserValueSet()) {
				canvas.drawRect(west + 1, north + 1, east - 1, south - 1, this.mUserSetPaint);
			}
			if (this.cell.isFlaky()) {
				canvas.drawRect(west + 1, north + 1, east - 1, south - 1, this.flakyPaint);
			}
			if (this.cell.isLastModified()) {
				canvas.drawRect(west + 1, north + 1, east - 1, south - 1, this.mLastModifiedPaint);
			}
			if (this.cell.isCheated()) {
				canvas.drawRect(west + 1, north + 1, east - 1, south - 1, this.mCheatedPaint);
			}
			if ((this.cell.isShowWarning() && GameVariant.getInstance()
					.showDupedDigits()) || this.cell.isInvalidHighlight()) {
				canvas.drawRect(west + 1, north + 1, east - 1, south - 1, this.mWarningPaint);
			}
			if (this.cell.isSelected()) {
				canvas.drawRect(west + 1, north + 1, east - 1, south - 1, this.mSelectedPaint);
			}
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
			borderPaint = this.mBorderPaint;
		}
		if (borderPaint != null) {
			canvas.drawLine(west, north, east, north, borderPaint);
		}
		
		// East
		borderPaint = this.getBorderPaint(Direction.EAST);
		if (!onlyBorders && this.cell.getCellBorders().getBorderType(Direction.EAST)
				.isHighlighted()) {
			borderPaint = this.mBorderPaint;
		}
		if (borderPaint != null) {
			canvas.drawLine(east, north, east, south, borderPaint);
		}
		
		// South
		borderPaint = this.getBorderPaint(Direction.SOUTH);
		if (!onlyBorders && this.cell.getCellBorders().getBorderType(Direction.SOUTH)
				.isHighlighted()) {
			borderPaint = this.mBorderPaint;
		}
		if (borderPaint != null) {
			canvas.drawLine(west, south, east, south, borderPaint);
		}
		
		// West
		borderPaint = this.getBorderPaint(Direction.WEST);
		if (!onlyBorders && this.cell.getCellBorders().getBorderType(Direction.WEST)
				.isHighlighted()) {
			borderPaint = this.mBorderPaint;
		}
		if (borderPaint != null) {
			canvas.drawLine(west, north, west, south, borderPaint);
		}
		
		if (onlyBorders) {
			return;
		}
		
		// Cell value
		if (this.cell.isUserValueSet()) {
			final int textSize = (int) (cellSize * 3 / 4);
			this.mValuePaint.setTextSize(textSize);
			
			final float leftOffset;
			
			if (this.cell.getUserValue() <= 9) {
				leftOffset = cellSize / 2 - textSize / 4;
			} else {
				leftOffset = cellSize / 2 - textSize / 2;
			}
			
			final float topOffset = cellSize / 2 + textSize * 2 / 5;
			
			canvas.drawText("" + this.cell.getUserValue(), this.mPosX + leftOffset,
					this.mPosY + topOffset, this.mValuePaint);
		}
		
		final int cageTextSize = (int) (cellSize / 3);
		this.mCageTextPaint.setTextSize(cageTextSize);
		// Cage text
		if (!this.getCell().getCageText().equals("")) {
			canvas.drawText(this.getCell()
					.getCageText(), this.mPosX + 2, this.mPosY + cageTextSize, this.mCageTextPaint);
			
			// canvas.drawText(this.mCageText, this.mPosX + 2, this.mPosY + 13, this.mCageTextPaint);
		}
		
		if (cell.getPossibles().size() > 0) {
			drawPossibleNumbers(canvas, cellSize);
		}
	}
	
	private void drawPossibleNumbers(Canvas canvas, float cellSize) {
		if (ApplicationPreferences.getInstance().show3x3Pencils()) {
			drawPossibleNumbersWithFixedGrid(canvas, cellSize);
		} else {
			drawPossibleNumbersDynamically(canvas, cellSize);
		}
	}
	
	private void drawPossibleNumbersDynamically(Canvas canvas, float cellSize) {
		this.mPossiblesPaint.setFakeBoldText(false);
		mPossiblesPaint.setTextSize((int) (cellSize / 4));
		
		List<SortedSet<Integer>> possiblesLines = new ArrayList<>();
		
		//adds all possible to one line
		TreeSet<Integer> currentLine = new TreeSet<>(cell.getPossibles());
		possiblesLines.add(currentLine);
		
		String currentLineText = getPossiblesLineText(currentLine);
		
		while (mPossiblesPaint.measureText(currentLineText) > cellSize - 6) {
			TreeSet<Integer> newLine = new TreeSet<>();
			possiblesLines.add(newLine);
			
			while (mPossiblesPaint.measureText(currentLineText) > cellSize - 6) {
				int firstDigitOfCurrentLine = currentLine.first();
				
				newLine.add(firstDigitOfCurrentLine);
				currentLine.remove(firstDigitOfCurrentLine);
				
				currentLineText = getPossiblesLineText(currentLine);
			}
			
			currentLine = newLine;
			currentLineText = getPossiblesLineText(currentLine);
		}
		
		int index = 0;
		
		Paint.FontMetricsInt metrics = mPossiblesPaint.getFontMetricsInt();
		
		int lineHeigth = metrics.ascent + metrics.bottom + metrics.top + metrics.leading + metrics.descent;
		
		for(SortedSet<Integer> possibleLine : possiblesLines) {
			canvas.drawText(getPossiblesLineText(possibleLine),
					mPosX + 3,
					mPosY + cellSize - 5 - lineHeigth * index,
					mPossiblesPaint);
			
			index++;
		}
		
		mPossiblesPaint.setStyle(Paint.Style.STROKE);
			
			/*canvas.drawRoundRect(mPosX + 3, mPosY + cellSize - 5,
					mPosX + 3 + textWidth,
					mPosY + cellSize - 5 -25,
					5, 5,
					mPossiblesPaint);*/
		
		mPossiblesPaint.setStyle(Paint.Style.FILL);
	}
	
	private void drawPossibleNumbersWithFixedGrid(Canvas canvas, float cellSize) {
		this.mPossiblesPaint.setFakeBoldText(true);
		this.mPossiblesPaint.setTextSize((int) (cellSize / 4.5));
		final int xOffset = (int) (cellSize / 3);
		final int yOffset = (int) (cellSize / 2) + 1;
		final float xScale = (float) 0.21 * cellSize;
		final float yScale = (float) 0.21 * cellSize;
		
		for (final int possible : cell.getPossibles()) {
			final float xPos = mPosX + xOffset + ((possible - 1) % 3) * xScale;
			final float yPos = mPosY + yOffset + ((possible - 1) / 3) * yScale;
			canvas.drawText(Integer.toString(possible), xPos, yPos, this.mPossiblesPaint);
		}
	}
	
	private String getPossiblesLineText(SortedSet<Integer> possibles) {
		return StringUtils.join(possibles, " ");
	}
	
	public GridCell getCell() {
		return this.cell;
	}
}