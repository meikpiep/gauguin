package com.holokenmod;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;

public class GridCellUI {
  private final GridCell cell;

  private float mPosX;
  private float mPosY;

  private GridUI mContext;

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
  
  private int mTheme;
  
  public GridCellUI(GridUI context, GridCell cell) {
    int gridSize = context.mGridSize;
    this.mContext = context;
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
    
    this.mUserSetPaint.setColor(0xFFFFFFFF);  //white   
    this.mWarningPaint.setColor(0x90ff4444);  //red
    this.mCheatedPaint.setColor(0x99d6b4e6);  //purple
    this.mSelectedPaint.setColor(Color.rgb(105,105,105));
    this.mLastModifiedPaint.setColor(0x44eeff33); //yellow
    
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

  public void setTheme(int theme) {
      this.mTheme = theme;
      if (theme == GridUI.THEME_LIGHT) {
          this.mUserSetPaint.setColor(0xFFFFFFFF);
          this.mBorderPaint.setColor(0xFF000000);
          this.mCageSelectedPaint.setColor(0xFF000000);
          this.mValuePaint.setColor(0xFF000000);
          this.mPossiblesPaint.setColor(0xFF000000);
          this.mCageTextPaint.setColor(0xFF0086B3);
      }
      else if (theme == GridUI.THEME_DARK) {
          this.mUserSetPaint.setColor(0xFF000000);
          this.mBorderPaint.setColor(0xFFFFFFFF);
          this.mCageSelectedPaint.setColor(0xFFFFFFFF);
          this.mValuePaint.setColor(0xFFFFFFFF);
          this.mPossiblesPaint.setColor(0xFFFFFFFF);
          this.mCageTextPaint.setColor(0xFF33b5e5);
      }
  }
  
  public String toString() {
    String str = "<cell:" + this.cell.getCellNumber() + " col:" + this.cell.getColumn() +
                  " row:" + this.cell.getRow() + " posX:" + this.mPosX + " posY:" +
                  this.mPosY + " val:" + this.cell.getValue() + ", userval: " + this.cell.getUserValue() + ">";
    return str;
  }
  
  /* Sets the cells border type to the given values.
   * 
   * Border is BORDER_NONE, BORDER_SOLID, BORDER_WARN or BORDER_CAGE_SELECTED.
   */
  public void setBorders(GridBorderType north, GridBorderType east, GridBorderType south, GridBorderType west) {
      this.cell.setCellBorders(new GridCellBorders(north, east, south, west));
  }
  
  /* Returns the Paint object for the given border of this cell. */
  private Paint getBorderPaint(Direction border) {
    switch (this.cell.getCellBorders().getBorderType(border)) {
      case BORDER_NONE:
        return null;
      case BORDER_SOLID :
        return this.mBorderPaint;
      case BORDER_WARN :
        return this.mWrongBorderPaint;
      case BORDER_CAGE_SELECTED :
          return this.mCageSelectedPaint;
    }
    return null;
  }

  public synchronized void setUserValue(int digit) {
      this.cell.clearPossibles();
      this.cell.setUserValue(digit);
      this.cell.setInvalidHighlight(false);
  }

  public synchronized void clearUserValue() {
      setUserValue(0);
  }

  public void onDraw(Canvas canvas, boolean onlyBorders) {
    
    // Calculate x and y for the cell origin (topleft)
    float cellSize = (float)this.mContext.getMeasuredWidth() / (float)this.mContext.mGridSize;
    this.mPosX = cellSize * this.cell.getColumn();
    this.mPosY = cellSize * this.cell.getRow();
    
    float north = this.mPosY;
    float south = this.mPosY + cellSize;
    float east = this.mPosX + cellSize;
    float west = this.mPosX;
    GridCellUI cellAbove = this.mContext.getCellAt(this.cell.getRow()-1, this.cell.getColumn());
    GridCellUI cellLeft = this.mContext.getCellAt(this.cell.getRow(), this.cell.getColumn()-1);
    GridCellUI cellRight = this.mContext.getCellAt(this.cell.getRow(), this.cell.getColumn()+1);
    GridCellUI cellBelow = this.mContext.getCellAt(this.cell.getRow()+1, this.cell.getColumn());

    if (!onlyBorders) {
        if (this.cell.isUserValueSet())
            canvas.drawRect(west+1, north+1, east-1, south-1, this.mUserSetPaint);
        if (this.cell.isLastModified())
            canvas.drawRect(west+1, north+1, east-1, south-1, this.mLastModifiedPaint);
        if (this.cell.isCheated())
            canvas.drawRect(west+1, north+1, east-1, south-1, this.mCheatedPaint);
        if ((this.cell.isShowWarning() && this.mContext.mDupedigits) || this.cell.isInvalidHighlight())
            canvas.drawRect(west+1, north+1, east-1, south-1, this.mWarningPaint);
        if (this.cell.isSelected())
            canvas.drawRect(west+1, north+1, east-1, south-1, this.mSelectedPaint);
    } else {
        if (this.cell.getCellBorders().getBorderType(Direction.NORTH).isHighlighted())
            if (cellAbove == null)
                north += 2;
            else
                north += 1;
        if (this.cell.getCellBorders().getBorderType(Direction.WEST).isHighlighted())
            if (cellLeft == null)
                west += 2;
            else
                west += 1;
        if (this.cell.getCellBorders().getBorderType(Direction.EAST).isHighlighted())
            if (cellRight == null)
                east -= 3;
            else
                east -= 2;
        if (this.cell.getCellBorders().getBorderType(Direction.SOUTH).isHighlighted())
            if (cellBelow == null)
                south -= 3;
            else
                south -= 2;
    }
    // North
    Paint borderPaint = this.getBorderPaint(Direction.NORTH);
    if (!onlyBorders && this.cell.getCellBorders().getBorderType(Direction.NORTH).isHighlighted())
        borderPaint = this.mBorderPaint;
    if (borderPaint != null) {
      canvas.drawLine(west, north, east, north, borderPaint);
    }
    
    // East
    borderPaint = this.getBorderPaint(Direction.EAST);
    if (!onlyBorders && this.cell.getCellBorders().getBorderType(Direction.EAST).isHighlighted())
        borderPaint = this.mBorderPaint;
    if (borderPaint != null)
      canvas.drawLine(east, north, east, south, borderPaint);
    
    // South
    borderPaint = this.getBorderPaint(Direction.SOUTH);
    if (!onlyBorders && this.cell.getCellBorders().getBorderType(Direction.SOUTH).isHighlighted())
        borderPaint = this.mBorderPaint;
    if (borderPaint != null)
      canvas.drawLine(west, south, east, south, borderPaint);
    
    // West
    borderPaint = this.getBorderPaint(Direction.WEST);
    if (!onlyBorders && this.cell.getCellBorders().getBorderType(Direction.WEST).isHighlighted())
        borderPaint = this.mBorderPaint;
    if (borderPaint != null) {
      canvas.drawLine(west, north, west, south, borderPaint);
    }
    
    if (onlyBorders)
        return;
    
    // Cell value
    if (this.cell.isUserValueSet()) {
        int textSize = (int)(cellSize*3/4);
        this.mValuePaint.setTextSize(textSize);
        float leftOffset = cellSize/2 - textSize/4;
        float topOffset = cellSize/2 + textSize*2/5;

        canvas.drawText("" + this.cell.getUserValue(), this.mPosX + leftOffset,
                this.mPosY + topOffset, this.mValuePaint);
    }
    
    int cageTextSize = (int)(cellSize/3);
    this.mCageTextPaint.setTextSize(cageTextSize);
    // Cage text
    if (!this.getCell().getCageText().equals("")) {
      canvas.drawText(this.getCell().getCageText(), this.mPosX + 2, this.mPosY + cageTextSize, this.mCageTextPaint);

      // canvas.drawText(this.mCageText, this.mPosX + 2, this.mPosY + 13, this.mCageTextPaint);
    }
    
    if (cell.getPossibles().size()>0) {
        Activity activity = mContext.mContext;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        if (prefs.getBoolean("pencil3x3", true)) {
            this.mPossiblesPaint.setFakeBoldText(true);
            this.mPossiblesPaint.setTextSize((int)(cellSize/4.5));
            int xOffset = (int) (cellSize/3);
            int yOffset = (int) (cellSize/2) + 1;
            float xScale = (float) 0.21 * cellSize;
            float yScale = (float) 0.21 * cellSize;
            for (int i = 0 ; i < cell.getPossibles().size() ; i++) {
                int possible = cell.getPossibles().get(i);
                float xPos = mPosX + xOffset + ((possible-1)%3)*xScale;
                float yPos = mPosY + yOffset + ((possible-1) /3)*yScale;
                   canvas.drawText(Integer.toString(possible), xPos, yPos, this.mPossiblesPaint);
            }
        }
        else {
            this.mPossiblesPaint.setFakeBoldText(false);
            mPossiblesPaint.setTextSize((int)(cellSize/4));
            String possibles = "";
            for (int i = 0 ; i < cell.getPossibles().size() ; i++)
                possibles += Integer.toString(cell.getPossibles().get(i));
            canvas.drawText(possibles, mPosX+3, mPosY + cellSize-5, mPossiblesPaint);
        }
    }
  }

  GridCell getCell() {
      return this.cell;
  }
}