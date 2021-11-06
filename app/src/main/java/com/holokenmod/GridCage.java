package com.holokenmod;

import android.util.Log;

import java.util.ArrayList;

public class GridCage {
  

    public static final int CAGE_1 = 0;

  // Action for the cage
  public GridCageAction mAction;
  public String mActionStr;
  // Number the action results in
  public int mResult;
  // List of cage's cells
  private ArrayList<GridCell> mCells;
  // Type of the cage
  public int mType;
  // Id of the cage
  public int mId;
  
  private final Grid grid;
  
  // User math is correct
  public boolean mUserMathCorrect;
  // Cage (or a cell within) is selected
  public boolean mSelected;

  public GridCage (Grid grid, int type) {
      this.grid = grid;
      mType = type;
      mUserMathCorrect = true;
      mSelected = false;
      mCells = new ArrayList<>();
  }
  
  public String toString() {
      String retStr = "";
      retStr += "Cage id: " + this.mId + ", Type: " + this.mType;
      retStr += ", Action: ";
      switch (this.mAction)
      {
      case ACTION_NONE:
          retStr += "None"; break;
      case ACTION_ADD:
          retStr += "Add"; break;
      case ACTION_SUBTRACT:
          retStr += "Subtract"; break;
      case ACTION_MULTIPLY:
          retStr += "Multiply"; break;
      case ACTION_DIVIDE:
          retStr += "Divide"; break;
      }

      retStr += ", ActionStr: " + this.mActionStr + ", Result: " + this.mResult;
      retStr += ", cells: " + getCellNumbers();

      return retStr;
  }

  /*
   * Generates the arithmetic for the cage, semi-randomly.
   * 
   * - If a cage has 3 or more cells, it can only be an add or multiply.
   * - else if the cells are evenly divisible, division is used, else
   *   subtraction.
   */
  public void setArithmetic(GridCageOperation operationSet) {
    this.mAction = null;
    if (this.mType == CAGE_1) {
      this.mAction = GridCageAction.ACTION_NONE;
      this.mActionStr = "";
      this.mResult = this.mCells.get(0).getValue();
      return;
    }
    double rand = RandomSingleton.getInstance().nextDouble();
    double addChance = 0.25;
    double multChance = 0.5;
    
    if (operationSet == GridCageOperation.OPERATIONS_ADD_SUB) {
        if (this.mCells.size() > 2) 
            addChance = 1.0;
        else
            addChance = 0.4;
        multChance = 0.0;
    }
    else if (operationSet == GridCageOperation.OPERATIONS_MULT) {
        addChance = 0.0;
        multChance = 1.0;
    }
    else if (this.mCells.size() > 2 || operationSet == GridCageOperation.OPERATIONS_ADD_MULT) { // force + and x only
        addChance = 0.5;
        multChance = 1.0;
    }

    if (rand <= addChance)
      this.mAction = GridCageAction.ACTION_ADD;
    else if (rand <= multChance)
      this.mAction = GridCageAction.ACTION_MULTIPLY;
    
    if (this.mAction == GridCageAction.ACTION_ADD) {
      int total = 0;
      for (GridCell cell : this.mCells) {
        total += cell.getValue();
      }
      this.mResult = total;
      this.mActionStr = "+";
    }
    if (this.mAction == GridCageAction.ACTION_MULTIPLY) {
      int total = 1;
      for (GridCell cell : this.mCells) {
        total *= cell.getValue();
      }
      this.mResult = total;
      this.mActionStr = "x";
    }
    if (this.mAction != null) {
      return;
    }

    if (this.mCells.size() < 2) {
        Log.d("KenKen", "Why only length 1? Type: " + this);
    }
    int cell1Value = this.mCells.get(0).getValue();
    int cell2Value = this.mCells.get(1).getValue();
    int higher = cell1Value;
    int lower = cell2Value;
    boolean canDivide = false;
    if (cell1Value < cell2Value) {
      higher = cell2Value;
      lower = cell1Value;
    }

    if (ApplicationPreferences.getInstance().getDigitSetting() == DigitSetting.FIRST_DIGIT_ONE && higher % lower == 0 && operationSet != GridCageOperation.OPERATIONS_ADD_SUB)
      canDivide = true;

    if (ApplicationPreferences.getInstance().getDigitSetting() == DigitSetting.FIRST_DIGIT_ZERO && lower > 0 && higher % lower == 0 && operationSet != GridCageOperation.OPERATIONS_ADD_SUB)
        canDivide = true;

      if (canDivide) {
      this.mResult = higher / lower;
      this.mAction = GridCageAction.ACTION_DIVIDE;
      // this.mCells.get(0).mCageText = this.mResult + "\367";
      this.mActionStr = "/";
    } else {
      this.mResult = higher - lower;
      this.mAction = GridCageAction.ACTION_SUBTRACT;
      this.mActionStr = "-";
    }
  }
  
  /*
   * Sets the cageId of the cage's cells.
   */
  public void setCageId(int id) {
    this.mId = id;
  }
  
  
  public boolean isAddMathsCorrect()
  {
      int total = 0;
      for (GridCell cell : this.mCells) {
          total += cell.getUserValue();
      }
      return (total == this.mResult);
  }

  public boolean isMultiplyMathsCorrect()
  {
      int total = 1;
      for (GridCell cell : this.mCells) {
          total *= cell.getUserValue();
      }
      return (total == this.mResult);
  }

  public boolean isDivideMathsCorrect()
  {
      if (this.mCells.size() != 2)
          return false;
      
      if (this.mCells.get(0).getUserValue() > this.mCells.get(1).getUserValue())
          return this.mCells.get(0).getUserValue() == (this.mCells.get(1).getUserValue() * this.mResult);
      else
          return this.mCells.get(1).getUserValue() == (this.mCells.get(0).getUserValue() * this.mResult);
  }

  public boolean isSubtractMathsCorrect()
  {
      if (this.mCells.size() != 2)
          return false;

      if (this.mCells.get(0).getUserValue() > this.mCells.get(1).getUserValue())
          return (this.mCells.get(0).getUserValue() - this.mCells.get(1).getUserValue()) == this.mResult;
      else
          return (this.mCells.get(1).getUserValue() - this.mCells.get(0).getUserValue()) == this.mResult;
  }
  
  // Returns whether the user values in the cage match the cage text
  public boolean isMathsCorrect() {
      if (this.mCells.size() == 1)
          return this.mCells.get(0).isUserValueCorrect();

      if (GameVariant.getInstance().showOperators()) {
          switch (this.mAction) {
                case ACTION_ADD :
                    return isAddMathsCorrect();
                case ACTION_MULTIPLY :
                    return isMultiplyMathsCorrect();
                case ACTION_DIVIDE :
                    return isDivideMathsCorrect();
                case ACTION_SUBTRACT :
                    return isSubtractMathsCorrect();
          }
      }
      else {
          return isAddMathsCorrect() || isMultiplyMathsCorrect() ||
                  isDivideMathsCorrect() || isSubtractMathsCorrect();

      }
      throw new RuntimeException("isSolved() got to an unreachable point " + 
              this.mAction + ": " + this.toString());
  }
  
  // Determine whether user entered values match the arithmetic.
  //
  // Only marks cells bad if all cells have a uservalue, and they dont
  // match the arithmetic hint.
  public void userValuesCorrect() {
    this.mUserMathCorrect = true;
    for (GridCell cell : this.mCells)
      if (!cell.isUserValueSet()) {
        this.setBorders();
        return;
      }
    this.mUserMathCorrect = this.isMathsCorrect();
    this.setBorders();
  }
  
  /*
   * Sets the borders of the cage's cells.
   */
  public void setBorders() {
    for (GridCell cell : this.mCells) {
        for(Direction direction : Direction.values()) {
            cell.getCellBorders().setBorderType(direction, GridBorderType.BORDER_NONE);
        }
      if (this.grid.getCage(cell.getRow()-1, cell.getColumn()) != this)
        if (!this.mUserMathCorrect && GameVariant.getInstance().showBadMaths())
            cell.getCellBorders().setBorderType(Direction.NORTH, GridBorderType.BORDER_WARN);
        else if (this.mSelected)
            cell.getCellBorders().setBorderType(Direction.NORTH, GridBorderType.BORDER_CAGE_SELECTED);
        else
            cell.getCellBorders().setBorderType(Direction.NORTH, GridBorderType.BORDER_SOLID);

      if (this.grid.getCage(cell.getRow(), cell.getColumn()+1) != this)
          if(!this.mUserMathCorrect && GameVariant.getInstance().showBadMaths())
              cell.getCellBorders().setBorderType(Direction.EAST, GridBorderType.BORDER_WARN);
          else if (this.mSelected)
              cell.getCellBorders().setBorderType(Direction.EAST, GridBorderType.BORDER_CAGE_SELECTED);
          else
              cell.getCellBorders().setBorderType(Direction.EAST, GridBorderType.BORDER_SOLID);

      if (this.grid.getCage(cell.getRow()+1, cell.getColumn()) != this)
        if(!this.mUserMathCorrect && GameVariant.getInstance().showBadMaths())
            cell.getCellBorders().setBorderType(Direction.SOUTH, GridBorderType.BORDER_WARN);
        else if (this.mSelected)
            cell.getCellBorders().setBorderType(Direction.SOUTH, GridBorderType.BORDER_CAGE_SELECTED);
        else
            cell.getCellBorders().setBorderType(Direction.SOUTH, GridBorderType.BORDER_SOLID);

      if (this.grid.getCage(cell.getRow(), cell.getColumn()-1) != this)
        if(!this.mUserMathCorrect && GameVariant.getInstance().showBadMaths())
            cell.getCellBorders().setBorderType(Direction.WEST, GridBorderType.BORDER_WARN);
        else if (this.mSelected)
            cell.getCellBorders().setBorderType(Direction.WEST, GridBorderType.BORDER_CAGE_SELECTED);
        else
            cell.getCellBorders().setBorderType(Direction.WEST, GridBorderType.BORDER_SOLID);
    }
  }


    public int getId() {
        return mId;
    }

    public void addCell(GridCell cell) {
        this.mCells.add(cell);
        cell.setCage(this);
    }

    public String getCellNumbers() {
        String numbers = "";

        for (GridCell cell : this.mCells) {
            numbers += cell.getCellNumber() + ",";
        }

        return numbers;
    }

    public void setCagetext(String cageText) {
        this.mCells.get(0).setCagetext(cageText);
    }

    public int getNumberOfCells() {
        return this.mCells.size();
    }

    public GridCell getCell(int cellNumber) {
        return this.mCells.get(cellNumber);
    }

    public ArrayList<GridCell> getCells() {
        return this.mCells;
    }
}