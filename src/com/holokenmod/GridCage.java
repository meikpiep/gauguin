package com.holokenmod;

import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

public class GridCage {
  
  public static final int OPERATIONS_ALL = 0;
  public static final int OPERATIONS_ADD_SUB = 1;
  public static final int OPERATIONS_ADD_MULT = 2;
  public static final int OPERATIONS_MULT = 3;
    
  public static final int ACTION_NONE = 0;
  public static final int ACTION_ADD = 1;
  public static final int ACTION_SUBTRACT = 2;
  public static final int ACTION_MULTIPLY = 3;
  public static final int ACTION_DIVIDE = 4;
  
  public static final int CAGE_UNDEF = -1;
  public static final int CAGE_1 = 0;

  // O = Origin (0,0) - must be the upper leftmost cell
  // X = Other cells used in cage
  public static final int [][][] CAGE_COORDS = new int[][][] {
      // O
      {{0,0}},
      // O
      // X
      {{0,0},{0,1}},
      // OX
      {{0,0},{1,0}},
      // O
      // X
      // X
      {{0,0},{0,1},{0,2}},
      // OXX
      {{0,0},{1,0},{2,0}},
      // O
      // XX 
      {{0,0},{0,1},{1,1}},
      // O
      //XX
      {{0,0},{0,1},{-1,1}},
      // OX
      //  X
      {{0,0},{1,0},{1,1}},
      // OX
      // X
      {{0,0},{1,0},{0,1}},
      // OX
      // XX
      {{0,0},{1,0},{0,1},{1,1}},
      // OX
      // X
      // X
      {{0,0},{1,0},{0,1},{0,2}},
      // OX
      //  X
      //  X
      //{{0,0},{1,0},{1,1},{1,2}},
      // O
      // X
      // XX
      //{{0,0},{0,1},{0,2},{1,2}},
      // O
      // X
      //XX
      {{0,0},{0,1},{0,2},{-1,2}},
      // OXX
      // X
      {{0,0},{1,0},{2,0},{0,1}},
      // OXX
      //   X
      {{0,0},{1,0},{2,0},{2,1}},
      // O
      // XXX
      /*{{0,0},{0,1},{1,1},{2,1}},
      //  O
      //XXX
      {{0,0},{-2,1},{-1,1},{0,1}},
      // O
      // XX
      // X
      {{0,0},{0,1},{0,2},{1,1}},
      // O
      //XX
      // X
      {{0,0},{0,1},{0,2},{-1,1}},
      // OXX
      //  X
      {{0,0},{1,0},{2,0},{1,1}},
      // O
      //XXX
      {{0,0},{-1,1},{0,1},{1,1}},
      // OXXX
      {{0,0},{1,0},{2,0},{3,0}},
      // O
      // X
      // X
      // X
      {{0,0},{0,1},{0,2},{0,3}},
      // O
      // XX
      //  X
      {{0,0},{0,1},{1,1},{1,2}},
      // O
      //XX
      //X
      {{0,0},{0,1},{-1,1},{-1,2}},
      // OX
      //  XX
      {{0,0},{1,0},{1,1},{2,1}},
      // OX
      //XX
      {{0,0},{1,0},{0,1},{-1,1}}*/
  };

  // Action for the cage
  public int mAction;
  public String mActionStr;
  // Number the action results in
  public int mResult;
  // List of cage's cells
  public ArrayList<GridCell> mCells;
  // Type of the cage
  public int mType;
  // Id of the cage
  public int mId;
  // Enclosing context
  public GridView mContext;
  // User math is correct
  public boolean mUserMathCorrect;
  // Cage (or a cell within) is selected
  public boolean mSelected;

  // Cached list of numbers which satisfy the cage's arithmetic
  private ArrayList<int[]> mPossibles;
  
  public GridCage (GridView context, int type) {
      this.mContext = context;
      mType = type;
      mPossibles = null;
      mUserMathCorrect = true;
      mSelected = false;
      mCells = new ArrayList<GridCell>();
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
      retStr += ", cells: ";
      for (GridCell cell : this.mCells)
          retStr += cell.mCellNumber + ", ";
      return retStr;
  }

  /*
   * Generates the arithmetic for the cage, semi-randomly.
   * 
   * - If a cage has 3 or more cells, it can only be an add or multiply.
   * - else if the cells are evenly divisible, division is used, else
   *   subtraction.
   */
  public void setArithmetic(int operationSet) {
    this.mAction = -1;
    if (this.mType == CAGE_1) {
      this.mAction = ACTION_NONE;
      this.mActionStr = "";
      this.mResult = this.mCells.get(0).mValue;
      return;
    }
    double rand = this.mContext.mRandom.nextDouble();
    double addChance = 0.25;
    double multChance = 0.5;
    
    if (operationSet == OPERATIONS_ADD_SUB) {
        if (this.mCells.size() > 2) 
            addChance = 1.0;
        else
            addChance = 0.4;
        multChance = 0.0;
    }
    else if (operationSet == OPERATIONS_MULT) {
        addChance = 0.0;
        multChance = 1.0;
    }
    else if (this.mCells.size() > 2 || operationSet == OPERATIONS_ADD_MULT) { // force + and x only
        addChance = 0.5;
        multChance = 1.0;
    }

    if (rand <= addChance)
      this.mAction = ACTION_ADD;
    else if (rand <= multChance)
      this.mAction = ACTION_MULTIPLY;
    
    if (this.mAction == ACTION_ADD) {
      int total = 0;
      for (GridCell cell : this.mCells) {
        total += cell.mValue;
      }
      this.mResult = total;
      this.mActionStr = "+";
    }
    if (this.mAction == ACTION_MULTIPLY) {
      int total = 1;
      for (GridCell cell : this.mCells) {
        total *= cell.mValue;
      }
      this.mResult = total;
      this.mActionStr = "x";
    }
    if (this.mAction > -1) {
      return;
    }

    if (this.mCells.size() < 2) {
        Log.d("KenKen", "Why only length 1? Type: " + this);
    }
    int cell1Value = this.mCells.get(0).mValue;
    int cell2Value = this.mCells.get(1).mValue;
    int higher = cell1Value;
    int lower = cell2Value;
    boolean canDivide = false;
    if (cell1Value < cell2Value) {
      higher = cell2Value;
      lower = cell1Value;
    }
    if (higher % lower == 0 && operationSet != OPERATIONS_ADD_SUB)
      canDivide = true;
    if (canDivide) {
      this.mResult = higher / lower;
      this.mAction = ACTION_DIVIDE;
      // this.mCells.get(0).mCageText = this.mResult + "\367";
      this.mActionStr = "/";
    } else {
      this.mResult = higher - lower;
      this.mAction = ACTION_SUBTRACT;
      this.mActionStr = "-";
    }
  }
  
  /*
   * Sets the cageId of the cage's cells.
   */
  public void setCageId(int id) {
    this.mId = id;
    for (GridCell cell : this.mCells)
      cell.mCageId = this.mId;
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

      if (this.mContext.mShowOperators) {
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
          if (isAddMathsCorrect() || isMultiplyMathsCorrect() ||
                  isDivideMathsCorrect() || isSubtractMathsCorrect())
              return true;
          else
              return false;

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
        for(int x = 0 ; x < 4 ; x++) {
            cell.mBorderTypes[x] = 0;
        }
      if (this.mContext.CageIdAt(cell.mRow-1, cell.mColumn) != this.mId)
        if (!this.mUserMathCorrect && this.mContext.mBadMaths)
          cell.mBorderTypes[0] = GridCell.BORDER_WARN;
        else if (this.mSelected)
            cell.mBorderTypes[0] = GridCell.BORDER_CAGE_SELECTED;
        else
            cell.mBorderTypes[0] = GridCell.BORDER_SOLID;
      
      if (this.mContext.CageIdAt(cell.mRow, cell.mColumn+1) != this.mId)
          if(!this.mUserMathCorrect && this.mContext.mBadMaths)
              cell.mBorderTypes[1] = GridCell.BORDER_WARN;
          else if (this.mSelected)
              cell.mBorderTypes[1] = GridCell.BORDER_CAGE_SELECTED;
          else
              cell.mBorderTypes[1] = GridCell.BORDER_SOLID;
      
      if (this.mContext.CageIdAt(cell.mRow+1, cell.mColumn) != this.mId)
        if(!this.mUserMathCorrect && this.mContext.mBadMaths)
          cell.mBorderTypes[2] = GridCell.BORDER_WARN;
        else if (this.mSelected)
            cell.mBorderTypes[2] = GridCell.BORDER_CAGE_SELECTED;
        else
            cell.mBorderTypes[2] = GridCell.BORDER_SOLID;
      
      if (this.mContext.CageIdAt(cell.mRow, cell.mColumn-1) != this.mId)
        if(!this.mUserMathCorrect && this.mContext.mBadMaths)
          cell.mBorderTypes[3] = GridCell.BORDER_WARN;
        else if (this.mSelected)
            cell.mBorderTypes[3] = GridCell.BORDER_CAGE_SELECTED;
        else
            cell.mBorderTypes[3] = GridCell.BORDER_SOLID;
    }
  }

  
  
  
  
  /****
   * AC: For hinting system?
   * 
   */
public ArrayList<int[]> getPossibleNums()
{
    if (mPossibles == null) {
        if (this.mContext.mShowOperators)
            mPossibles = setPossibleNums();
        else
            mPossibles = setPossibleNumsNoOperator();
    }
    return mPossibles;
}

private ArrayList<int[]> setPossibleNumsNoOperator()
{
    ArrayList<int[]> AllResults = new ArrayList<int[]>();

    if (this.mAction == ACTION_NONE) {
        assert (mCells.size() == 1);
        int number[] = {mResult};
        AllResults.add(number);
        return AllResults;
    }
    
    if (mCells.size() == 2) {
        for (int i1=1; i1<=this.mContext.mGridSize; i1++)
            for (int i2=i1+1; i2<=this.mContext.mGridSize; i2++)
                if (i2 - i1 == mResult || i1 - i2 == mResult || mResult*i1 == i2 || 
                        mResult*i2 == i1 || i1+i2 == mResult || i1*i2 == mResult) {
                    int numbers[] = {i1, i2};
                    AllResults.add(numbers);
                    numbers = new int[] {i2, i1};
                    AllResults.add(numbers);
                }
        return AllResults;
    }

    // ACTION_ADD:
    AllResults = getalladdcombos(this.mContext.mGridSize,mResult,mCells.size());
    
    // ACTION_MULTIPLY:
    ArrayList<int[]> multResults = getallmultcombos(this.mContext.mGridSize,mResult,mCells.size());
    
    // Combine Add & Multiply result sets
    for (int[] possibleset: multResults)
    {
        boolean foundset = false;
        for (int[] currentset: AllResults) {
            if (Arrays.equals(possibleset, currentset)) {
                foundset = true;
                break;
            }
        }
        if (!foundset)
            AllResults.add(possibleset);
    }
        
    return AllResults;
}
  
/*
 * Generates all combinations of numbers which satisfy the cage's arithmetic
 * and MathDoku constraints i.e. a digit can only appear once in a column/row 
 */
private ArrayList<int[]> setPossibleNums()
{
    ArrayList<int[]> AllResults = new ArrayList<int[]>();

    switch (this.mAction) {
    case ACTION_NONE:
        assert (mCells.size() == 1);
        int number[] = {mResult};
        AllResults.add(number);
        break;
      case ACTION_SUBTRACT:
          assert(mCells.size() == 2);
          for (int i1=1; i1<=this.mContext.mGridSize; i1++)
              for (int i2=i1+1; i2<=this.mContext.mGridSize; i2++)
                  if (i2 - i1 == mResult || i1 - i2 == mResult) {
                      int numbers[] = {i1, i2};
                      AllResults.add(numbers);
                      numbers = new int[] {i2, i1};
                      AllResults.add(numbers);
                  }
          break;
      case ACTION_DIVIDE:
          assert(mCells.size() == 2);
          for (int i1=1; i1<=this.mContext.mGridSize; i1++)
              for (int i2=i1+1; i2<=this.mContext.mGridSize; i2++)
                  if (mResult*i1 == i2 || mResult*i2 == i1) {
                      int numbers[] = {i1, i2};
                      AllResults.add(numbers);
                      numbers = new int[] {i2, i1};
                      AllResults.add(numbers);
                  }
          break;
      case ACTION_ADD:
          AllResults = getalladdcombos(this.mContext.mGridSize,mResult,mCells.size());
          break;
      case ACTION_MULTIPLY:
          AllResults = getallmultcombos(this.mContext.mGridSize,mResult,mCells.size());
          break;
    }
    return AllResults;
}

// The following two variables are required by the recursive methods below.
// They could be passed as parameters of the recursive methods, but this
// reduces performance.
private int[] numbers;
private ArrayList<int[]> result_set;

private ArrayList<int[]> getalladdcombos (int max_val, int target_sum, int n_cells)
{
    numbers = new int[n_cells];
    result_set = new ArrayList<int[]> ();
    getaddcombos(max_val, target_sum, n_cells);
    return result_set;
}

/*
 * Recursive method to calculate all combinations of digits which add up to target
 * 
 * @param max_val        maximum permitted value of digit (= dimension of grid)
 * @param target_sum    the value which all the digits should add up to
 * @param n_cells        number of digits still to select
 */
private void getaddcombos(int max_val, int target_sum, int n_cells)
{
    for (int n=1; n<= max_val; n++)
    {
        if (n_cells == 1)
        {
            if (n == target_sum) {
                numbers[0] = n;
                if (satisfiesConstraints(numbers))
                    result_set.add(numbers.clone());
            }
        }
        else {
            numbers[n_cells-1] = n;
            getaddcombos(max_val, target_sum-n, n_cells-1);
        }
    }
    return;
}

private ArrayList<int[]> getallmultcombos (int max_val, int target_sum, int n_cells)
{
    numbers = new int[n_cells];
    result_set = new ArrayList<int[]> ();
    getmultcombos(max_val, target_sum, n_cells);
    
    return result_set;
}

/*
 * Recursive method to calculate all combinations of digits which multiply up to target
 * 
 * @param max_val        maximum permitted value of digit (= dimension of grid)
 * @param target_sum    the value which all the digits should multiply up to
 * @param n_cells        number of digits still to select
 */
private void getmultcombos(int max_val, int target_sum, int n_cells)
{
    for (int n=1; n<= max_val; n++)
    {
        if (target_sum % n != 0)
            continue;
        
        if (n_cells == 1)
        {
            if (n == target_sum) {
                numbers[0] = n;
                if (satisfiesConstraints(numbers))
                    result_set.add(numbers.clone());
            }
        }
        else {
            numbers[n_cells-1] = n;
            getmultcombos(max_val, target_sum/n, n_cells-1);
        }
    }
    return;
}

/*
 * Check whether the set of numbers satisfies all constraints
 * Looking for cases where a digit appears more than once in a column/row
 * Constraints:
 * 0 -> (mGridSize * mGridSize)-1 = column constraints
 * (each column must contain each digit) 
 * mGridSize * mGridSize -> 2*(mGridSize * mGridSize)-1 = row constraints
 * (each row must contain each digit) 
 */
private boolean satisfiesConstraints(int[] test_nums) {
    
    boolean constraints[] = new boolean[mContext.mGridSize*mContext.mGridSize*2];
    int constraint_num;
    for (int i = 0; i<this.mCells.size(); i++) {
        constraint_num = mContext.mGridSize*(test_nums[i]-1) + mCells.get(i).mColumn;
        if (constraints[constraint_num])
            return false;
        else
            constraints[constraint_num]= true;
        constraint_num = mContext.mGridSize*mContext.mGridSize + mContext.mGridSize*(test_nums[i]-1) + mCells.get(i).mRow;
        if (constraints[constraint_num])
            return false;
        else
            constraints[constraint_num]= true;
    }
    return true;
}

}