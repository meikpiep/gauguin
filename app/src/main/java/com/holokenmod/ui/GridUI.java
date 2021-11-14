package com.holokenmod.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCell;
import com.holokenmod.Theme;
import com.holokenmod.creation.GridCreator;

import java.util.ArrayList;

public class GridUI extends View implements OnTouchListener {
	
	// Used to avoid redrawing or saving grid during creation of new grid
	public final Object mLock = new Object();
	private final ArrayList<GridCellUI> mCells = new ArrayList<>();
	private boolean selectorShown = false;
	public long mDate;
	private OnSolvedListener mSolvedListener;
	private OnGridTouchListener mTouchedListener;
	private float mTrackPosX;
	private float mTrackPosY;
	private int mCurrentWidth;
	private Paint mGridPaint;
	private Paint mBorderPaint;
	private int mBackgroundColor;
	private Grid grid;
	
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
	
	public void initGridView() {
		this.mSolvedListener = null;
		
		//default is holo light
		this.mGridPaint = new Paint();
		this.mGridPaint.setColor(0x90e0bf9f); //light brown
		this.mGridPaint.setStrokeWidth(0);
		this.mGridPaint.setPathEffect(new DashPathEffect(new float[]{3, 3}, 0));
		
		this.mBorderPaint = new Paint();
		this.mBorderPaint.setColor(0xFF000000);
		this.mBorderPaint.setStrokeWidth(3);
		this.mBorderPaint.setStyle(Style.STROKE);
		this.mBorderPaint.setAntiAlias(false);
		this.mBorderPaint.setPathEffect(null);
		
		this.mBackgroundColor = 0xFFFFFFFF;
		
		this.mCurrentWidth = 0;
		this.setOnTouchListener(this);
	}
	
	public void setTheme(final Theme theme) {
		if (theme == Theme.LIGHT) {
			this.mBackgroundColor = 0xFFf3efe7; //off-white
			this.mBorderPaint.setColor(0xFF000000);
			this.mGridPaint.setColor(0x90e0bf9f); //light brown
			
		} else if (theme == Theme.DARK) {
			this.mBackgroundColor = 0xFF272727;
			this.mBorderPaint.setColor(0xFFFFFFFF);
			this.mGridPaint.setColor(0x90555555); //light gray
		}
		
		if (this.getMeasuredHeight() < 150) {
			this.mBorderPaint.setStrokeWidth(1);
		} else {
			this.mBorderPaint.setStrokeWidth(3);
		}
		
		
		if (this.mCells != null) {
			for (final GridCellUI cell : this.mCells) {
				cell.setTheme(theme);
			}
		}
		this.invalidate();
	}
	
	public void reCreate() {
		if (grid.getGridSize() < 4) {
			return;
		}
		
		synchronized (mLock) {    // Avoid redrawing at the same time as creating puzzle
			final GridCreator creator = new GridCreator(grid.getGridSize());
			this.grid = creator.create();
			
			this.mCells.clear();
			
			for (final GridCell cell : grid.getCells()) {
				this.mCells.add(new GridCellUI(grid, cell));
			}
			
			this.mTrackPosX = 0;
			this.mTrackPosY = 0;
			this.grid.setActive(true);
			this.selectorShown = false;
		}
	}
	
	public void clearUserValues() {
		grid.clearUserValues();
		
		this.invalidate();
	}
	
	public void clearLastModified() {
		grid.clearLastModified();
		
		this.invalidate();
	}
	
	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		// Our target grid is a square, measuring 80% of the minimum dimension
		final int measuredWidth = measure(widthMeasureSpec);
		final int measuredHeight = measure(heightMeasureSpec);
		
		final int dim = Math.min(measuredWidth, measuredHeight);
		
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
		
		synchronized (mLock) {    // Avoid redrawing at the same time as creating puzzle
			if (grid.getGridSize() < 4) {
				return;
			}
			
			final int width = getMeasuredWidth();
			
			if (width != this.mCurrentWidth) {
				this.mCurrentWidth = width;
			}
			
			// Fill canvas background
			canvas.drawColor(this.mBackgroundColor);
			
			// Check cage correctness
			for (final GridCage cage : grid.getCages()) {
				cage.userValuesCorrect();
			}
			
			//setCageText();
			
			// Draw (dashed) grid
			for (int i = 1; i < grid.getGridSize(); i++) {
				final float pos = ((float) this.mCurrentWidth / (float) grid.getGridSize()) * i;
				canvas.drawLine(0, pos, this.mCurrentWidth, pos, this.mGridPaint);
				canvas.drawLine(pos, 0, pos, this.mCurrentWidth, this.mGridPaint);
			}
			
			// Calculate x and y for the cell origin (topleft)
			final float cellSize = (float) this.getMeasuredWidth() / (float) this.grid
					.getGridSize();
			
			// Draw cells
			for (final GridCellUI cell : this.mCells) {
				cell.getCell().setShowWarning((cell.getCell().isUserValueSet() && grid
						.getNumValueInCol(cell.getCell()) > 1) ||
						(cell.getCell().isUserValueSet() && grid
								.getNumValueInRow(cell.getCell()) > 1));
				cell.onDraw(canvas, false, cellSize);
			}
			
			// Draw borders
			canvas.drawLine(0, 1, this.mCurrentWidth, 1, this.mBorderPaint);
			canvas.drawLine(1, 0, 1, this.mCurrentWidth, this.mBorderPaint);
			canvas.drawLine(0, this.mCurrentWidth - 2, this.mCurrentWidth, this.mCurrentWidth - 2, this.mBorderPaint);
			canvas.drawLine(this.mCurrentWidth - 2, 0, this.mCurrentWidth - 2, this.mCurrentWidth, this.mBorderPaint);
			
			// Draw cells
			for (final GridCellUI cell : this.mCells) {
				cell.onDraw(canvas, true, cellSize);
			}
			
			
			if (grid.isActive() && grid.isSolved()) {
				if (grid.getSelectedCell() != null) {
					grid.getSelectedCell().setSelected(false);
					grid.getSelectedCell().getCage().setSelected(false);
					this.invalidate();
				}
				if (this.mSolvedListener != null) {
					this.mSolvedListener.puzzleSolved();
				}
				grid.setActive(false);
			}
		}
	}
	
	// Given a cell number, returns origin x,y coordinates.
	private float[] CellToCoord(final int cell) {
		final float xOrd;
		final float yOrd;
		final int cellWidth = this.mCurrentWidth / grid.getGridSize();
		xOrd = ((float) cell % grid.getGridSize()) * cellWidth;
		yOrd = (cell / grid.getGridSize() * cellWidth);
		return new float[]{xOrd, yOrd};
	}
	
	// Opposite of above - given a coordinate, returns the cell number within.
	private GridCell CoordToCell(final float x, final float y) {
		final int row = (int) ((y / (float) this.mCurrentWidth) * grid.getGridSize());
		final int col = (int) ((x / (float) this.mCurrentWidth) * grid.getGridSize());
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
		
		int row = (int) ((size - (size - y)) / (size / grid.getGridSize()));
		if (row > grid.getGridSize() - 1) {
			row = grid.getGridSize() - 1;
		}
		if (row < 0) {
			row = 0;
		}
		
		int col = (int) ((size - (size - x)) / (size / grid.getGridSize()));
		if (col > grid.getGridSize() - 1) {
			col = grid.getGridSize() - 1;
		}
		if (col < 0) {
			col = 0;
		}
		
		// We can now get the cell.
		final GridCell cell = grid.getCellAt(row, col);
		grid.setSelectedCell(cell);
		
		final float[] cellPos = this.CellToCoord(cell.getCage().getId());
		this.mTrackPosX = cellPos[0];
		this.mTrackPosY = cellPos[1];
		
		for (final GridCellUI c : this.mCells) {
			c.getCell().setSelected(false);
			c.getCell().getCage().setSelected(false);
		}
		if (this.mTouchedListener != null) {
			grid.getSelectedCell().setSelected(true);
			grid.getSelectedCell().getCage().setSelected(true);
			this.mTouchedListener.gridTouched(grid.getSelectedCell());
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
			if (this.mTouchedListener != null) {
				grid.getSelectedCell().setSelected(true);
				this.mTouchedListener.gridTouched(grid.getSelectedCell());
			}
			return true;
		}
		// A multiplier amplifies the trackball event values
		int trackMult = 70;
		switch (grid.getGridSize()) {
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
		this.mTrackPosX += x * trackMult;
		this.mTrackPosY += y * trackMult;
		final GridCell cell = this.CoordToCell(this.mTrackPosX, this.mTrackPosY);
		if (cell == null) {
			this.mTrackPosX -= x * trackMult;
			this.mTrackPosY -= y * trackMult;
			return true;
		}
		// Set the cell as selected
		if (grid.getSelectedCell() != null) {
			grid.getSelectedCell().setSelected(false);
			if (grid.getSelectedCell() != cell) {
				this.mTouchedListener.gridTouched(cell);
			}
		}
		for (final GridCellUI c : this.mCells) {
			c.getCell().setSelected(false);
			c.getCell().getCage().setSelected(false);
		}
		grid.setSelectedCell(cell);
		cell.setSelected(true);
		grid.getSelectedCell().getCage().setSelected(true);
		invalidate();
		return true;
	}
	
	// Solve the puzzle by setting the Uservalue to the actual value
	public void solve(final boolean solveGrid) {
		grid.solve(solveGrid);
		
		this.invalidate();
	}
	
	// Highlight those cells where the user has made a mistake
	public void markInvalidChoices() {
		grid.markInvalidChoices();
		
		invalidate();
	}
	
	public void setSolvedHandler(final OnSolvedListener listener) {
		this.mSolvedListener = listener;
	}
	
	public void addCell(final GridCellUI cellUI) {
		this.mCells.add(cellUI);
	}
	
	public void resetCells() {
		this.mCells.clear();
	}
	
	public void setOnGridTouchListener(final OnGridTouchListener listener) {
		this.mTouchedListener = listener;
	}
	
	public Grid getGrid() {
		return grid;
	}
	
	public void setGrid(final Grid grid) {
		this.grid = grid;
	}
	
	@FunctionalInterface
	public interface OnSolvedListener {
		void puzzleSolved();
	}
	
	@FunctionalInterface
	public interface OnGridTouchListener {
		void gridTouched(GridCell cell);
	}
	
	void setSelectorShown(boolean shown) {
		this.selectorShown = shown;
	}
	
	boolean isSelectorShown() {
		return this.selectorShown;
	}
}