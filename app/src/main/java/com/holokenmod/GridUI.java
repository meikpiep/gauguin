package com.holokenmod;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.srlee.DLX.DLX.SolveType;
import com.srlee.DLX.MathDokuDLX;

import java.util.ArrayList;
import java.util.Random;

public class GridUI extends View implements OnTouchListener  {

  public static final int THEME_LIGHT = 0;
  public static final int THEME_DARK = 1;

  private OnSolvedListener mSolvedListener;
  private OnGridTouchListener mTouchedListener;

  public int mGridSize;
  public long mPlayTime;
  
  // Random generator
  public Random mRandom;
  
  public Activity mContext;

  public ArrayList<GridCage> mCages;
  
  public ArrayList<GridCellUI> mCells;
  
  public boolean mActive;
  
  public boolean mSelectorShown = false;
  
  private float mTrackPosX;
  private float mTrackPosY;
  
  public GridCellUI mSelectedCell;
  
  Resources res = getResources();
  private int mCurrentWidth;
  private Paint mGridPaint;
  private Paint mBorderPaint;
  private int mBackgroundColor;

  public boolean mDupedigits;
  public boolean mBadMaths;
  public boolean mShowOperators;

  public long mDate;

  // Used to avoid redrawing or saving grid during creation of new grid
  public final Object mLock = new Object();
  private Grid grid;

  public GridUI(Context context) {
    super(context);
    initGridView();
  }
  
  public GridUI(Context context, AttributeSet attrs) {
    super(context, attrs);
    initGridView();
  }
  
  public GridUI(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initGridView();
  }
  
  
  public void initGridView() {

    this.mSolvedListener = null;
    this.mDupedigits = true;
    this.mBadMaths = true;
    this.mShowOperators = true;
    this.mPlayTime = 0;

    //default is holo light
    this.mGridPaint = new Paint();
    this.mGridPaint.setColor(0x90e0bf9f); //light brown
    this.mGridPaint.setStrokeWidth(0);
    this.mGridPaint.setPathEffect(new DashPathEffect(new float[] {3, 3}, 0));
    
    this.mBorderPaint = new Paint();
    this.mBorderPaint.setColor(0xFF000000);
    this.mBorderPaint.setStrokeWidth(3);
    this.mBorderPaint.setStyle(Style.STROKE);
    this.mBorderPaint.setAntiAlias(false);
    this.mBorderPaint.setPathEffect(null);
    
    this.mBackgroundColor = 0xFFFFFFFF;

    this.mCurrentWidth = 0;
    this.mGridSize = 0;
    this.mActive = false;
    this.setOnTouchListener(this);
    
  }
  
  public void setTheme(int theme) {
      if (theme == THEME_LIGHT) {
          this.mBackgroundColor = 0xFFf3efe7; //off-white
          this.mBorderPaint.setColor(0xFF000000);
          this.mGridPaint.setColor(0x90e0bf9f); //light brown

      } else if (theme == THEME_DARK) {
          this.mBackgroundColor = 0xFF272727;
          this.mBorderPaint.setColor(0xFFFFFFFF);
          this.mGridPaint.setColor(0x90555555); //light gray
      }

      if (this.getMeasuredHeight() < 150)
            this.mBorderPaint.setStrokeWidth(1);
      else
            this.mBorderPaint.setStrokeWidth(3);


      if (this.mCells != null)
          for (GridCellUI cell : this.mCells)
              cell.setTheme(theme);
      this.invalidate();
  }
  
  public void reCreate() {
      synchronized (mLock) {    // Avoid redrawing at the same time as creating puzzle
          int num_solns;
          int num_attempts = 0;
          this.mRandom = new Random();
          if (this.mGridSize < 4) return;
          do {
              this.mCells = new ArrayList<GridCellUI>();

              ArrayList<GridCell> cells = new ArrayList<GridCell>();

              int cellnum = 0;

              for (int i = 0 ; i < this.mGridSize * this.mGridSize ; i++) {
                  GridCell cell = new GridCell(cellnum++, this.mGridSize);
                  cells.add(cell);

                  this.mCells.add(new GridCellUI(this, cell));
              }

              this.grid = new Grid(cells);

              randomiseGrid();
              this.mTrackPosX = this.mTrackPosY = 0;
              this.mCages = new ArrayList<GridCage>();
              CreateCages();
              num_attempts++;
              MathDokuDLX mdd = new MathDokuDLX(this.mGridSize, this.mCages);
              // Stop solving as soon as we find multiple solutions
              num_solns = mdd.Solve(SolveType.MULTIPLE);
              Log.d ("MathDoku", "Num Solns = " + num_solns);
          } while (num_solns > 1);
          Log.d ("MathDoku", "Num Attempts = " + num_attempts);
          this.mActive = true;
          this.mSelectorShown = false;
          //this.setTheme(this.mTheme);
      }
  }

  // Returns cage id of cell at row, column
  // Returns -1 if not a valid cell or cage
  public int CageIdAt(int row, int column) {
      if (row < 0 || row >= mGridSize || column < 0 || column >= mGridSize)
          return -1;
      return this.mCells.get(column + row*this.mGridSize).getCell().getCageId();
  }
  
  public int CreateSingleCages(int operationSet) {
    int singles = this.mGridSize / 2;
    boolean RowUsed[] = new boolean[mGridSize];
    boolean ColUsed[] = new boolean[mGridSize];
    boolean ValUsed[] = new boolean[mGridSize];
    for (int i = 0 ; i < singles ; i++) {
        GridCellUI cell;
        while (true) {
            cell = mCells.get(mRandom.nextInt(mGridSize * mGridSize));
            if (!RowUsed[cell.getCell().getRow()] && !ColUsed[cell.getCell().getRow()] && !ValUsed[cell.getCell().getValue()-1])
                break;
        }
        ColUsed[cell.getCell().getColumn()] = true;
        RowUsed[cell.getCell().getRow()] = true;
        ValUsed[cell.getCell().getValue()-1] = true;
        GridCage cage = new GridCage(this, GridCage.CAGE_1);
        cage.mCells.add(cell.getCell());
        cage.setArithmetic(operationSet);
        cage.setCageId(i);
        this.mCages.add(cage);
    }
    return singles;
  }
   
  /* Take a filled grid and randomly create cages */
  public void CreateCages() {

      boolean restart;

      do {
          restart = false;
          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.mContext);
          int operationSet = prefs.getInt("mathmodes", 0);
            
          int cageId = CreateSingleCages(operationSet);
          for (int cellNum = 0 ; cellNum < this.mCells.size() ; cellNum++) {
              GridCell cell = this.mCells.get(cellNum).getCell();
              if (cell.CellInAnyCage())
                  continue; // Cell already in a cage, skip

              ArrayList<Integer> possible_cages = getvalidCages(cell);
              if (possible_cages.size() == 1) {    // Only possible cage is a single
                  ClearAllCages();
                  restart=true;
                  break;
              }

              // Choose a random cage type from one of the possible (not single cage)
              int cage_type = possible_cages.get(mRandom.nextInt(possible_cages.size()-1)+1);
              GridCage cage = new GridCage(this, cage_type);
              int [][]cage_coords = GridCage.CAGE_COORDS[cage_type];
              for (int coord_num = 0; coord_num < cage_coords.length; coord_num++) {
                  int col = cell.getColumn() + cage_coords[coord_num][0];
                  int row = cell.getRow() + cage_coords[coord_num][1];
                  cage.mCells.add(getCellAt(row, col).getCell());
              }

              cage.setArithmetic(operationSet);  // Make the maths puzzle
              cage.setCageId(cageId++);  // Set cage's id
              this.mCages.add(cage);  // Add to the cage list
          }
      } while (restart);
      for (GridCage cage : this.mCages)
          cage.setBorders();
      setCageText();
  }
  
  public ArrayList<Integer> getvalidCages(GridCell origin)
  {
      if (origin.CellInAnyCage())
          return null;
      
      boolean [] InvalidCages = new boolean[GridCage.CAGE_COORDS.length];
      
      // Don't need to check first cage type (single)
      for (int cage_num=1; cage_num < GridCage.CAGE_COORDS.length; cage_num++) {
          int [][]cage_coords = GridCage.CAGE_COORDS[cage_num];
          // Don't need to check first coordinate (0,0)
          for (int coord_num = 1; coord_num < cage_coords.length; coord_num++) {
              int col = origin.getColumn() + cage_coords[coord_num][0];
              int row = origin.getRow() + cage_coords[coord_num][1];
              GridCellUI c = getCellAt(row, col);
              if (c == null || c.getCell().CellInAnyCage()) {
                  InvalidCages[cage_num] = true;
                  break;
              }
          }
      }

      ArrayList<Integer> valid =  new ArrayList<Integer>();
      for (int i=0; i<GridCage.CAGE_COORDS.length; i++)
          if (!InvalidCages[i])
              valid.add(i);
      
      return valid;
  }
  
  public void setCageText() {
      for (GridCage cage : this.mCages) {
          if (this.mShowOperators)
              cage.mCells.get(0).setCagetext(cage.mResult + cage.mActionStr);
          else
              cage.mCells.get(0).setCagetext(cage.mResult + "");
      }
  }
  
  public void ClearAllCages() {
      for (GridCellUI cell : this.mCells) {
          cell.getCell().setCageId(-1);
          cell.getCell().setCagetext("");
      }
      this.mCages = new ArrayList<GridCage>();
  }
  
  public void clearUserValues() {
      for (GridCellUI cell : this.mCells) {
          cell.clearUserValue();
          cell.getCell().setCheated(false);
      }
      if (this.mSelectedCell != null) {
          this.mSelectedCell.getCell().setSelected(false);
          this.mCages.get(this.mSelectedCell.getCell().getCageId()).mSelected = false;
      }
      this.invalidate();
  }
  
  public void clearLastModified() {
      for (GridCellUI cell : this.mCells) {
          cell.getCell().setLastModified(false);
      }

      this.invalidate();
  }
  
  public GridCellUI getCellAt(int row, int column) {
      if (row < 0 || row >= mGridSize)
          return null;
      if (column < 0 || column >= mGridSize)
          return null;
      
      return this.mCells.get(column + row*this.mGridSize);
  }
  
  /*
   * Fills the grid with random numbers, per the rules:
   * 
   * - 1 to <rowsize> on every row and column
   * - No duplicates in any row or column.
   */
  public void randomiseGrid() {
    int attempts;
    for (int value = 1 ; value < this.mGridSize+1 ; value++) {
      for (int row = 0 ; row < this.mGridSize ; row++) {
        attempts = 20;
        GridCellUI cell;
        int column;
        while (true) {
          column = this.mRandom.nextInt(this.mGridSize);
          cell = getCellAt(row, column);
          if (--attempts == 0)
            break;
          if (cell.getCell().getValue() != 0)
            continue;
          if (valueInColumn(column, value))
            continue;
          break;
        }
        if (attempts == 0) {
          this.clearValue(value--);
          break;
        }
        cell.getCell().setValue(value);
        //Log.d("KenKen", "New cell: " + cell);
      }
    }
  }
  /* Clear any cells containing the given number. */
  public void clearValue(int value) {
    for (GridCellUI cell : this.mCells)
      if (cell.getCell().getValue() == value)
        cell.getCell().setValue(0);
  }
  
  /* Determine if the given value is in the given row */
  public boolean valueInRow(int row, int value) {
    for (GridCellUI cell : this.mCells)
      if (cell.getCell().getRow() == row && cell.getCell().getValue() == value)
        return true;
    return false;
  }
  
  /* Determine if the given value is in the given column */
  public boolean valueInColumn(int column, int value) {
        for (int row=0; row< mGridSize; row++)
            if (this.mCells.get(column+row*mGridSize).getCell().getValue() == value)
                return true;
        return false;
  }

  
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // Our target grid is a square, measuring 80% of the minimum dimension
    int measuredWidth = measure(widthMeasureSpec);
    int measuredHeight = measure(heightMeasureSpec);

    int dim = Math.min(measuredWidth, measuredHeight);

    setMeasuredDimension(dim, dim);
  }
  private int measure(int measureSpec) {

    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);

    if (specMode == MeasureSpec.UNSPECIFIED)
      return 180;
    else
      return specSize;
  }
  
  @Override
  protected void onDraw(Canvas canvas) {
      synchronized (mLock) {    // Avoid redrawing at the same time as creating puzzle
          if (this.mGridSize < 4) return;
          if (this.mCages == null) return;

          int width = getMeasuredWidth();

          if (width != this.mCurrentWidth)
              this.mCurrentWidth = width;

          // Fill canvas background
          canvas.drawColor(this.mBackgroundColor);

          // Check cage correctness
          for (GridCage cage : this.mCages)
              cage.userValuesCorrect();

          //setCageText();

          // Draw (dashed) grid
          for (int i = 1 ; i < this.mGridSize ; i++) {
              float pos = ((float)this.mCurrentWidth / (float)this.mGridSize) * i;
              canvas.drawLine(0, pos, this.mCurrentWidth, pos, this.mGridPaint);
              canvas.drawLine(pos, 0, pos, this.mCurrentWidth, this.mGridPaint);
          }

          // Draw cells
          for (GridCellUI cell : this.mCells) {
              cell.getCell().setShowWarning((cell.getCell().isUserValueSet() && this.getNumValueInCol(cell) > 1) ||
                      (cell.getCell().isUserValueSet() && this.getNumValueInRow(cell) > 1));
              cell.onDraw(canvas, false);
          }

          // Draw borders
          canvas.drawLine(0, 1, this.mCurrentWidth, 1, this.mBorderPaint);
          canvas.drawLine(1, 0, 1, this.mCurrentWidth, this.mBorderPaint);
          canvas.drawLine(0, this.mCurrentWidth-2, this.mCurrentWidth, this.mCurrentWidth-2, this.mBorderPaint);
          canvas.drawLine(this.mCurrentWidth-2, 0, this.mCurrentWidth-2, this.mCurrentWidth, this.mBorderPaint);

          // Draw cells
          for (GridCellUI cell : this.mCells) {
              cell.onDraw(canvas, true);
          }

          
          if (this.mActive && this.isSolved()) {
              if (this.mSelectedCell != null) {
                  this.mSelectedCell.getCell().setSelected(false);
                  this.mCages.get(this.mSelectedCell.getCell().getCageId()).mSelected = false;
                  this.invalidate();
              }
              if (this.mSolvedListener != null)
                      this.mSolvedListener.puzzleSolved();
              this.mActive = false;
          }
      }
  }
  
  // Given a cell number, returns origin x,y coordinates.
  private float[] CellToCoord(int cell) {
    float xOrd;
    float yOrd;
    int cellWidth = this.mCurrentWidth / this.mGridSize;
    xOrd = ((float)cell % this.mGridSize) * cellWidth;
    yOrd = (cell / this.mGridSize * cellWidth);
    return new float[] {xOrd, yOrd};
  }
  
  // Opposite of above - given a coordinate, returns the cell number within.
  private GridCellUI CoordToCell(float x, float y) {
      int row = (int) ((y / (float)this.mCurrentWidth) * this.mGridSize);
      int col = (int) ((x / (float)this.mCurrentWidth) * this.mGridSize);
      // Log.d("KenKen", "Track x/y = " + col + " / " + row);
      return getCellAt(row, col);
  }

  public boolean onTouch(View arg0, MotionEvent event) {
      if (event.getAction() != MotionEvent.ACTION_DOWN)
          return false;
      if (!this.mActive)
          return false;

      // Find out where the grid was touched.
      float x = event.getX();
      float y = event.getY();
      int size = getMeasuredWidth();
    
    int row = (int)((size - (size-y))/(size/this.mGridSize));
    if (row > this.mGridSize-1) row = this.mGridSize-1;
    if (row < 0) row = 0;

    int col = (int)((size - (size-x))/(size/this.mGridSize));
    if (col > this.mGridSize-1) col = this.mGridSize-1;
    if (col < 0) col = 0;
    
    // We can now get the cell.
    GridCellUI cell = getCellAt(row, col);
    this.mSelectedCell = cell;
    
    float[] cellPos = this.CellToCoord(cell.getCell().getCageId());
    this.mTrackPosX = cellPos[0];
    this.mTrackPosY = cellPos[1];

    for (GridCellUI c : this.mCells) {
        c.getCell().setSelected(false);
        this.mCages.get(c.getCell().getCageId()).mSelected = false;
    }
    if (this.mTouchedListener != null) {
        this.mSelectedCell.getCell().setSelected(true);
        this.mCages.get(this.mSelectedCell.getCell().getCageId()).mSelected = true;
        this.mTouchedListener.gridTouched(this.mSelectedCell);
    }
    invalidate();
    return false;
  }
  
  // Handle trackball, both press down, and scrolling around to
  // select a cell.
  public boolean onTrackballEvent(MotionEvent event) {
    if (!this.mActive || this.mSelectorShown)
        return false;
    // On press event, take selected cell, call touched listener
    // which will popup the digit selector.
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
        if (this.mTouchedListener != null) {
            this.mSelectedCell.getCell().setSelected(true);
            this.mTouchedListener.gridTouched(this.mSelectedCell);
        }
        return true;
    }
    // A multiplier amplifies the trackball event values
    int trackMult = 70;
    switch (this.mGridSize) {
        case 5:
            trackMult = 60;
            break;
        case 6:
            trackMult = 50;
            break;
        case 7:
            trackMult = 40;
            break;
        case 8:
            trackMult = 40;
    }
    // Fetch the trackball position, work out the cell it's at
    float x = event.getX();
    float y = event.getY();
    this.mTrackPosX += x*trackMult;
    this.mTrackPosY += y*trackMult;
    GridCellUI cell = this.CoordToCell(this.mTrackPosX, this.mTrackPosY);
    if (cell == null) {
        this.mTrackPosX -= x*trackMult;
        this.mTrackPosY -= y*trackMult;
        return true;
    }
    // Set the cell as selected
    if (this.mSelectedCell != null) {
        this.mSelectedCell.getCell().setSelected(false);
        if (this.mSelectedCell != cell)
            this.mTouchedListener.gridTouched(cell);
    }
    for (GridCellUI c : this.mCells) {
        c.getCell().setSelected(false);
        this.mCages.get(c.getCell().getCageId()).mSelected = false;
    }
    this.mSelectedCell = cell;
    cell.getCell().setSelected(true);
    this.mCages.get(this.mSelectedCell.getCell().getCageId()).mSelected = true;
    invalidate();
    return true;
  }
  
  
  // Return the number of times a given user value is in a row
  public int getNumValueInRow(GridCellUI ocell) {
      int count = 0;
      for (GridCellUI cell : this.mCells)
          if (cell.getCell().getRow() == ocell.getCell().getRow() &&
                    cell.getCell().getUserValue() == ocell.getCell().getUserValue())
              count++;
      return count;
  }
  // Return the number of times a given user value is in a column
  public int getNumValueInCol(GridCellUI ocell) {
      int count = 0;

      for (GridCellUI cell : this.mCells)
          if (cell.getCell().getColumn() == ocell.getCell().getColumn() &&
                  cell.getCell().getUserValue() == ocell.getCell().getUserValue())
              count++;
      return count;
  }
  
  // Return the cells with same possibles in row and column
  public ArrayList<GridCellUI> getPossiblesInRowCol(GridCellUI ocell) {
      ArrayList<GridCellUI> possiblesRowCol = new ArrayList<GridCellUI>();
      int userValue = ocell.getCell().getUserValue();
      for (GridCellUI cell : this.mCells)
    	  if (cell.getCell().isPossible(userValue))
    		  if (cell.getCell().getRow() == ocell.getCell().getRow() || cell.getCell().getColumn() == ocell.getCell().getColumn())
    			  possiblesRowCol.add(cell);
      return possiblesRowCol;
  }
  
  // Return the cells with same possibles in row and column
  public ArrayList<GridCellUI> getSinglePossibles() {
      ArrayList<GridCellUI> singlePossibles = new ArrayList<GridCellUI>();
      for (GridCellUI cell : this.mCells)
          if (cell.getCell().getPossibles().size() == 1)
              singlePossibles.add(cell);
      return singlePossibles;
  }
  
  // Solve the puzzle by setting the Uservalue to the actual value
  public void Solve(boolean solveGrid, boolean markCheated) {
      if (this.mSelectedCell != null) {
    	  ArrayList<GridCell> solvecell = this.mCages.get(
    			  this.mSelectedCell.getCell().getCageId()).mCells;
          if (solveGrid) {
              solvecell = new ArrayList<>();

              for(GridCellUI cellUI : this.mCells) {
                  solvecell.add(cellUI.getCell());
              }
          }

          for (GridCell cell : solvecell) {
              if (!cell.isUserValueCorrect()) {
                  cell.setUserValue(cell.getValue());
                  if (markCheated)
                      cell.setCheated(true);
              }
          }
          this.mSelectedCell.getCell().setSelected(false);
          this.mCages.get(this.mSelectedCell.getCell().getCageId()).mSelected = false;
      }
      this.invalidate();
  }
  
  // Returns whether the puzzle is solved.
  public boolean isSolved() {
      for (GridCellUI cell : this.mCells)
          if (!cell.getCell().isUserValueCorrect())
              return false;
      return true;
  }
  
  // Returns whether the puzzle used cheats.
  public int countCheated() {
      int counter = 0;
      for (GridCellUI cell : this.mCells)
          if (cell.getCell().isCheated())
              counter++;
      return counter;
  }
  
  // Checks whether the user has made any mistakes
  public int[] countMistakes() {
      int counter[] = {0,0};
      for (GridCellUI cell : this.mCells) {
          if (cell.getCell().isUserValueSet()) {
              counter[1]++;
              if (cell.getCell().getUserValue() != cell.getCell().getValue())
                  counter[0]++;
          }
      }
      return counter;
  }
  
  // Highlight those cells where the user has made a mistake
  public void markInvalidChoices() {
      boolean isValid = true;
      for (GridCellUI cell : this.mCells)
          if (cell.getCell().isUserValueSet() && cell.getCell().getUserValue() != cell.getCell().getValue()) {
              cell.getCell().setInvalidHighlight(true);
              isValid = false;
          }

      if (!isValid)
          invalidate();
      return;
  }
  
  // Return the list of cells that are highlighted as invalid
  public ArrayList<GridCell> invalidsHighlighted()
  {
      ArrayList<GridCell> invalids = new ArrayList<>();
      for (GridCellUI cell : this.mCells)
          if (cell.getCell().isInvalidHighlight())
              invalids.add(cell.getCell());
      
      return invalids;
  }
  
  // Return the list of cells that are highlighted as invalid
  public ArrayList<GridCell> cheatedHighlighted()
  {
      ArrayList<GridCell> cheats = new ArrayList<>();
      for (GridCellUI cell : this.mCells)
          if (cell.getCell().isCheated())
             cheats.add(cell.getCell());
      
      return cheats;
  }
  
  public void setSolvedHandler(OnSolvedListener listener) {
      this.mSolvedListener = listener;
  }

  public void setGrid(Grid grid) {
      this.grid = grid;
  }

    @FunctionalInterface
  public interface OnSolvedListener {
      void puzzleSolved();
  }
  
  public void setOnGridTouchListener(OnGridTouchListener listener) {
      this.mTouchedListener = listener;
  }

  @FunctionalInterface
  public interface OnGridTouchListener {
      void gridTouched(GridCellUI cell);
  }
}