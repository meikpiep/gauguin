package com.holokenmod.ui.grid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.android.material.color.MaterialColors;
import com.holokenmod.R;
import com.holokenmod.game.Game;
import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridCage;
import com.holokenmod.grid.GridCell;
import com.holokenmod.grid.GridView;
import com.holokenmod.options.ApplicationPreferences;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

public class GridUI extends View implements OnTouchListener, GridView {
	
	static final float CORNER_RADIUS = 15;
	static final int BORDER_WIDTH = 3;
	
	private ArrayList<GridCellUI> cells = new ArrayList<>();
	private boolean selectorShown = false;
	private OnGridTouchListener touchedListener;
	private float trackPosX;
	private float trackPosY;
	private int currentWidth;
	private Paint gridPaint;
	private Paint borderPaint;
	private Paint outerBorderPaint;
	private int backgroundColor;
	private Grid grid;
	private GridPaintHolder paintHolder;
	private boolean previewMode = false;
	private boolean previewStillCalculating = false;
	private int cellSizePercent = 100;
	
	public GridUI(final Context context) {
		super(context);
		initGridView();
	}
	
	public GridUI(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		initGridView();
	}
	
	public GridUI(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		initGridView();
	}
	
	private void initGridView() {
		this.gridPaint = new Paint();
		this.gridPaint.setStrokeWidth(0);
		this.gridPaint.setPathEffect(null);
		
		this.borderPaint = new Paint();
		this.borderPaint.setStrokeWidth(3);
		this.borderPaint.setStyle(Style.STROKE);
		this.borderPaint.setAntiAlias(false);
		this.borderPaint.setPathEffect(null);
		
		this.outerBorderPaint = new Paint();
		this.outerBorderPaint.setStrokeWidth(3);
		this.outerBorderPaint.setStyle(Style.STROKE);
		this.outerBorderPaint.setAntiAlias(false);
		this.outerBorderPaint.setPathEffect(null);
		
		this.currentWidth = 0;
		this.setOnTouchListener(this);
	}
	
	public void initializeWithGame(Game game) {
		setOnGridTouchListener(cell -> {
			setSelectorShown(true);
			game.selectCell();
		});
		
		boolean rmpencil = ApplicationPreferences.getInstance().removePencils();
		setOnLongClickListener(v -> game.setSinglePossibleOnSelectedCell(rmpencil));
		
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
	public void updateTheme() {
		this.backgroundColor = MaterialColors.getColor(this, R.attr.colorSurface);
		this.borderPaint.setColor(MaterialColors.getColor(this, R.attr.colorOnBackground));
		this.outerBorderPaint.setColor(MaterialColors.compositeARGBWithAlpha(MaterialColors.getColor(this, R.attr.colorOnBackground), 200));
		this.gridPaint.setColor(MaterialColors.compositeARGBWithAlpha(MaterialColors.getColor(this, R.attr.colorOnBackground), 100));
		
		if (this.getMeasuredHeight() < 150) {
			this.borderPaint.setStrokeWidth(1);
		} else {
			this.borderPaint.setStrokeWidth(3);
		}
		
		this.invalidate();
	}
	
	public void setPreviewMode(boolean previewMode) {
		this.previewMode = previewMode;
	}
	
	public void reCreate() {
		if (grid.getGridSize().getAmountOfNumbers() < 2) {
			return;
		}
		
		//synchronized (lock) {    // Avoid redrawing at the same time as creating puzzle
			rebuildCellsFromGrid();
			
			this.trackPosX = 0;
			this.trackPosY = 0;
			this.selectorShown = false;
		//}
	}
	
	public void rebuildCellsFromGrid() {
		this.cells = new ArrayList<>();
		
		paintHolder = new GridPaintHolder(this);
		
		for (final GridCell cell : grid.getCells()) {
			this.cells.add(new GridCellUI(this, cell, paintHolder));
		}
	}
	
	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		final int measuredWidth = measure(widthMeasureSpec);
		final int measuredHeight = measure(heightMeasureSpec);
		
		final int dim = Math.min(measuredWidth, measuredHeight) * cellSizePercent / 100;
		
		setMeasuredDimension(dim, dim);
	}
	
	private int measure(final int measureSpec) {
		final int specMode = MeasureSpec.getMode(measureSpec);
		final int specSize = MeasureSpec.getSize(measureSpec);
		
		if (specMode == MeasureSpec.UNSPECIFIED) {
			return 180;
		} else {
			return specSize;
		}
	}
	
	@Override
	protected void onDraw(final Canvas canvas) {
		if (grid == null) {
			return;
		}
		
		if (grid.getGridSize().getAmountOfNumbers() < 2) {
			return;
		}
		
		this.currentWidth = getMeasuredWidth();
		
		canvas.drawColor(this.backgroundColor);
		
		for (final GridCage cage : grid.getCages()) {
			cage.userValuesCorrect();
		}
		
		final float cellSize = getCellSize();
		
		drawDashedGrid(canvas, cellSize);
		
		// Draw cells
		for (final GridCellUI cell : this.cells) {
			cell.getCell().setShowWarning((cell.getCell().isUserValueSet() && grid
					.getNumValueInCol(cell.getCell()) > 1) ||
					(cell.getCell().isUserValueSet() && grid
							.getNumValueInRow(cell.getCell()) > 1));
			cell.onDraw(canvas, false, cellSize);
		}
		
		drawGridBorders(canvas, cellSize);
		
		// Draw cells
		for (final GridCellUI cell : this.cells) {
			cell.onDraw(canvas, true, cellSize);
		}
		
		if (previewMode) {
			drawPreviewMode(canvas);
		}
	}
	
	private void drawGridBorders(Canvas canvas, float cellSize) {
		// bottom right edge
		canvas.drawArc(cellSize * grid.getGridSize().getWidth() - 2 * CORNER_RADIUS + 2 + BORDER_WIDTH,
				cellSize * grid.getGridSize().getHeight() - 2 * CORNER_RADIUS + 2 + BORDER_WIDTH,
				cellSize * grid.getGridSize().getWidth() + 2 + BORDER_WIDTH,
				cellSize * grid.getGridSize().getHeight() + 2 + BORDER_WIDTH,
				0,
				90,
				false,
				outerBorderPaint);
		
		// bottom left edge
		canvas.drawArc(2,
				cellSize * grid.getGridSize().getHeight() - 2 * CORNER_RADIUS + 2 + BORDER_WIDTH,
				2 + 2 * CORNER_RADIUS,
				cellSize * grid.getGridSize().getHeight() + 2 + BORDER_WIDTH,
				90,
				90,
				false,
				outerBorderPaint);
		
		// top left edge
		canvas.drawArc(2,
				2,
				2 + 2 * CORNER_RADIUS,
				2 + 2 * CORNER_RADIUS,
				180,
				90,
				false,
				outerBorderPaint);
		
		// top right edge
		canvas.drawArc(cellSize * grid.getGridSize().getWidth() - 2 * CORNER_RADIUS + 2 + BORDER_WIDTH,
				2,
				cellSize * grid.getGridSize().getWidth() + 2 + BORDER_WIDTH,
				2 + 2 * CORNER_RADIUS,
				270,
				90,
				false,
				outerBorderPaint);
		
		// top
		canvas.drawLine(CORNER_RADIUS + 2 + BORDER_WIDTH,
				2,
				cellSize * grid.getGridSize().getWidth() - CORNER_RADIUS + 2 + BORDER_WIDTH,
				2,
				outerBorderPaint);
		
		// bottom
		canvas.drawLine(CORNER_RADIUS + BORDER_WIDTH,
				cellSize * grid.getGridSize().getHeight() + 2 + BORDER_WIDTH,
				cellSize * grid.getGridSize().getWidth() - CORNER_RADIUS + 2 + BORDER_WIDTH,
				cellSize * grid.getGridSize().getHeight() + 2 + BORDER_WIDTH,
				outerBorderPaint);
		
		// left
		canvas.drawLine(2,
				CORNER_RADIUS + 2 + BORDER_WIDTH,
				2,
				cellSize * grid.getGridSize().getHeight() - CORNER_RADIUS + 2 + BORDER_WIDTH,
				outerBorderPaint);
		
		// right
		canvas.drawLine(cellSize * grid.getGridSize().getWidth() + 2 + BORDER_WIDTH,
				CORNER_RADIUS + 2 + BORDER_WIDTH,
				cellSize * grid.getGridSize().getWidth() + 2 + BORDER_WIDTH,
				cellSize * grid.getGridSize().getHeight() - CORNER_RADIUS + 2 + BORDER_WIDTH,
				outerBorderPaint);
	}
	
	private void drawPreviewMode(Canvas canvas) {
		Path previewPath = new Path();
		
		float distanceFromEdge = getResources().getDisplayMetrics().density * 60;
		float WIDTH = distanceFromEdge * 0.6f;
		
		previewPath.moveTo(0, distanceFromEdge + WIDTH);
		previewPath.lineTo(distanceFromEdge + WIDTH, 0);
		previewPath.lineTo(distanceFromEdge, 0);
		previewPath.lineTo(distanceFromEdge, 0);
		previewPath.lineTo(0, distanceFromEdge);
		
		final int cageTextSize = (int) (distanceFromEdge / 3);
		paintHolder.textOfSelectedCellPaint.setTextSize(cageTextSize);
		
		String previewText = "Preview";
		
		if (previewStillCalculating) {
			previewText += "...";
		}
		
		canvas.drawPath(previewPath, paintHolder.mSelectedPaint);
		canvas.drawTextOnPath(previewText, previewPath, distanceFromEdge * 0.4f, distanceFromEdge * -0.08f, paintHolder.textOfSelectedCellPaint);
	}
	
	private int getCellSize() {
		final float cellSizeWidth = ((float) this.getMeasuredWidth() - 2 * BORDER_WIDTH) / (float) this.grid
				.getGridSize().getWidth();
		final float cellSizeHeight = ((float) this.getMeasuredHeight() - 2 * BORDER_WIDTH) / (float) this.grid
				.getGridSize().getHeight();
		
		return (int) Math.min(cellSizeWidth, cellSizeHeight);
	}
	
	private void drawDashedGrid(Canvas canvas, float cellSize) {
		for (int i = 1; i < grid.getGridSize().getHeight(); i++) {
			canvas.drawLine(BORDER_WIDTH, cellSize * i + BORDER_WIDTH,
					cellSize * grid.getGridSize().getWidth(), cellSize * i + BORDER_WIDTH,
					this.gridPaint);
		}
		
		for (int i = 1; i < grid.getGridSize().getWidth(); i++) {
			canvas.drawLine(cellSize * i + BORDER_WIDTH, BORDER_WIDTH,
					cellSize * i + BORDER_WIDTH, cellSize * grid.getGridSize().getHeight() + BORDER_WIDTH,
					this.gridPaint);
		}
	}
	
	// Given a cell number, returns origin x,y coordinates.
	private Pair<Float,Float> CellToCoord(final int cell) {
		final float xOrd;
		final float yOrd;
		final int cellWidth = getCellSize();
		xOrd = ((float) cell % grid.getGridSize().getWidth()) * cellWidth + CORNER_RADIUS;
		yOrd = (cell / grid.getGridSize().getWidth() * cellWidth) + CORNER_RADIUS;
		return Pair.of(xOrd, yOrd);
	}
	
	// Opposite of above - given a coordinate, returns the cell number within.
	private GridCell CoordToCell(final float x, final float y) {
		final int row = (int) ((y - CORNER_RADIUS / (float) this.currentWidth) * grid.getGridSize().getWidth());
		final int col = (int) ((x - CORNER_RADIUS / (float) this.currentWidth) * grid.getGridSize().getWidth());
		// Log.d("KenKen", "Track x/y = " + col + " / " + row);
		return grid.getCellAt(row, col);
	}
	
	public boolean onTouch(final View arg0, final MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			return false;
		}
		if (!grid.isActive()) {
			return false;
		}
		
		// Find out where the grid was touched.
		final float x = event.getX();
		final float y = event.getY();
		final int size = getMeasuredWidth();
		
		int row = (int) ((size - (size - y)) / (size / grid.getGridSize().getAmountOfNumbers()));
		if (row > grid.getGridSize().getHeight() - 1) {
			row = grid.getGridSize().getHeight() - 1;
		}
		if (row < 0) {
			row = 0;
		}
		
		int col = (int) ((size - (size - x)) / (size / grid.getGridSize().getAmountOfNumbers()));
		if (col > grid.getGridSize().getWidth() - 1) {
			col = grid.getGridSize().getWidth() - 1;
		}
		if (col < 0) {
			col = 0;
		}
		
		// We can now get the cell.
		final GridCell cell = grid.getCellAt(row, col);
		grid.setSelectedCell(cell);
		
		final Pair<Float,Float> cellPos = this.CellToCoord(cell.getCellNumber());
		this.trackPosX = cellPos.getLeft();
		this.trackPosY = cellPos.getRight();
		
		for (final GridCellUI c : this.cells) {
			c.getCell().setSelected(false);
			
			if (c.getCell().getCage() != null) {
				c.getCell().getCage().setSelected(false);
			}
		}
		if (this.touchedListener != null) {
			grid.getSelectedCell().setSelected(true);
			grid.getSelectedCell().getCage().setSelected(true);
			this.touchedListener.gridTouched(grid.getSelectedCell());
		}
		invalidate();
		return false;
	}
	
	// Handle trackball, both press down, and scrolling around to
	// select a cell.
	public boolean onTrackballEvent(final MotionEvent event) {
		if (!grid.isActive() || this.selectorShown) {
			return false;
		}
		// On press event, take selected cell, call touched listener
		// which will popup the digit selector.
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (this.touchedListener != null) {
				grid.getSelectedCell().setSelected(true);
				this.touchedListener.gridTouched(grid.getSelectedCell());
			}
			return true;
		}
		// A multiplier amplifies the trackball event values
		int trackMult = 70;
		switch (grid.getGridSize().getAmountOfNumbers()) {
			case 5:
				trackMult = 60;
				break;
			case 6:
				trackMult = 50;
				break;
			case 7:
			case 8:
				trackMult = 40;
				break;
		}
		// Fetch the trackball position, work out the cell it's at
		final float x = event.getX();
		final float y = event.getY();
		this.trackPosX += x * trackMult;
		this.trackPosY += y * trackMult;
		final GridCell cell = this.CoordToCell(this.trackPosX, this.trackPosY);
		if (cell == null) {
			this.trackPosX -= x * trackMult;
			this.trackPosY -= y * trackMult;
			return true;
		}
		// Set the cell as selected
		if (grid.getSelectedCell() != null) {
			grid.getSelectedCell().setSelected(false);
			if (grid.getSelectedCell() != cell) {
				this.touchedListener.gridTouched(cell);
			}
		}
		for (final GridCellUI c : this.cells) {
			c.getCell().setSelected(false);
			c.getCell().getCage().setSelected(false);
		}
		grid.setSelectedCell(cell);
		cell.setSelected(true);
		grid.getSelectedCell().getCage().setSelected(true);
		invalidate();
		return true;
	}
	
	public void setOnGridTouchListener(final OnGridTouchListener listener) {
		this.touchedListener = listener;
	}
	
	public Grid getGrid() {
		return grid;
	}
	
	public void setGrid(final Grid grid) {
		this.grid = grid;
	}
	
	public boolean isPreviewMode() {
		return previewMode;
	}
	
	public void setPreviewStillCalculating(boolean previewStillCalculating) {
		this.previewStillCalculating = previewStillCalculating;
	}
	
	public void setCellSizePercent(int cellSizePercent) {
		this.cellSizePercent = cellSizePercent;
	}
	
	@FunctionalInterface
	public interface OnGridTouchListener {
		void gridTouched(GridCell cell);
	}
	
	public void setSelectorShown(boolean shown) {
		this.selectorShown = shown;
	}
	
	public boolean isSelectorShown() {
		return this.selectorShown;
	}
}