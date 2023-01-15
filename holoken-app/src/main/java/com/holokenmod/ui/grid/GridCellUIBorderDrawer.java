package com.holokenmod.ui.grid;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.holokenmod.Direction;
import com.holokenmod.grid.GridCell;

public class GridCellUIBorderDrawer {
	private final GridCell cell;
	private final GridCellUI cellUI;
	private final GridPaintHolder paintHolder;
	
	public GridCellUIBorderDrawer(GridCellUI cellUI, GridPaintHolder paintHolder) {
		this.cellUI = cellUI;
		this.cell = cellUI.getCell();
		this.paintHolder = paintHolder;
	}
	
	void drawBorders(Canvas canvas, boolean onlyBorders) {
		float north = cellUI.getNorthPixel();
		float south = cellUI.getSouthPixel();
		float east = cellUI.getEastPixel();
		float west = cellUI.getWestPixel();
		
		final boolean cellAbove = cell.hasNeighbor(Direction.NORTH);
		final boolean cellLeft = cell.hasNeighbor(Direction.WEST);
		final boolean cellRight = cell.hasNeighbor(Direction.EAST);
		final boolean cellBelow = cell.hasNeighbor(Direction.SOUTH);
		
		if (onlyBorders) {
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
			if (!cellAbove && !cellRight) {
				canvas.drawLine(west, north, east - GridUI.CORNER_RADIUS, north, borderPaint);
				canvas.drawArc(east - 2 * GridUI.CORNER_RADIUS, north,
						east, north + 2 * GridUI.CORNER_RADIUS,
						270,
						90,
						false,
						borderPaint);
			} else if (!cellAbove && !cellLeft) {
				canvas.drawLine(west + GridUI.CORNER_RADIUS, north, east, north, borderPaint);
				canvas.drawArc(west, north,
						west + 2 * GridUI.CORNER_RADIUS, north + 2 * GridUI.CORNER_RADIUS,
						180,
						90,
						false,
						borderPaint);
			} else {
				canvas.drawLine(west, north, east, north, borderPaint);
			}
		}
		
		// East
		borderPaint = this.getBorderPaint(Direction.EAST);
		if (!onlyBorders && this.cell.getCellBorders().getBorderType(Direction.EAST)
				.isHighlighted()) {
			borderPaint = paintHolder.mBorderPaint;
		}
		if (borderPaint != null) {
			if (!cellAbove && !cellRight) {
				canvas.drawLine(east, north + GridUI.CORNER_RADIUS, east, south, borderPaint);
			} else if (!cellBelow && !cellRight) {
				canvas.drawLine(east, north, east, south - GridUI.CORNER_RADIUS, borderPaint);
			} else {
				canvas.drawLine(east, north, east, south, borderPaint);
			}
		}
		
		// South
		borderPaint = this.getBorderPaint(Direction.SOUTH);
		if (!onlyBorders && this.cell.getCellBorders().getBorderType(Direction.SOUTH)
				.isHighlighted()) {
			borderPaint = paintHolder.mBorderPaint;
		}
		if (borderPaint != null) {
			if (!cellBelow && !cellRight) {
				canvas.drawLine(west, south, east - GridUI.CORNER_RADIUS, south, borderPaint);
				canvas.drawArc(east - 2 * GridUI.CORNER_RADIUS, south - 2 * GridUI.CORNER_RADIUS,
						east, south,
						0,
						90,
						false,
						borderPaint);
			} else if (!cellBelow && !cellLeft) {
				canvas.drawLine(west + GridUI.CORNER_RADIUS, south, east, south, borderPaint);
				canvas.drawArc(west, south - 2 * GridUI.CORNER_RADIUS,
						west + 2 * GridUI.CORNER_RADIUS, south,
						90,
						90,
						false,
						borderPaint);
			} else {
				canvas.drawLine(west, south, east, south, borderPaint);
			}
		}
		
		// West
		borderPaint = this.getBorderPaint(Direction.WEST);
		if (!onlyBorders && this.cell.getCellBorders().getBorderType(Direction.WEST)
				.isHighlighted()) {
			borderPaint = paintHolder.mBorderPaint;
		}
		if (borderPaint != null) {
			if (!cellAbove && !cellLeft) {
				canvas.drawLine(west, north + GridUI.CORNER_RADIUS, west, south, borderPaint);
			} else if (!cellBelow && !cellLeft) {
				canvas.drawLine(west, north, west, south - GridUI.CORNER_RADIUS, borderPaint);
			} else {
				canvas.drawLine(west, north, west, south, borderPaint);
			}
		}
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
}
