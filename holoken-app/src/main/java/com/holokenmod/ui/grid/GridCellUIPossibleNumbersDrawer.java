package com.holokenmod.ui.grid;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.holokenmod.grid.GridCell;
import com.holokenmod.options.ApplicationPreferences;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class GridCellUIPossibleNumbersDrawer {
	private final GridCell cell;
	private final GridCellUI cellUI;
	private final GridPaintHolder paintHolder;
	
	public GridCellUIPossibleNumbersDrawer(GridCellUI cellUI, GridPaintHolder paintHolder) {
		this.cellUI = cellUI;
		this.cell = cellUI.getCell();
		this.paintHolder = paintHolder;
	}
	
	void drawPossibleNumbers(Canvas canvas, float cellSize) {
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
		
		while (paint.measureText(currentLineText) > cellSize - 8) {
			TreeSet<Integer> newLine = new TreeSet<>();
			possiblesLines.add(newLine);
			
			while (paint.measureText(currentLineText) > cellSize - 8) {
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
					cellUI.getPositionX() + 4,
					cellUI.getPositionY() + cellSize - 6 - lineHeigth * index,
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
			final float xPos = cellUI.getPositionX() + xOffset + ((possible - 1) % 3) * xScale;
			final float yPos = cellUI.getPositionY() + yOffset + ((possible - 1) / 3) * yScale;
			canvas.drawText(Integer.toString(possible), xPos, yPos, paint);
		}
	}
	
	private String getPossiblesLineText(SortedSet<Integer> possibles) {
		return StringUtils.join(possibles, "|");
	}
}
