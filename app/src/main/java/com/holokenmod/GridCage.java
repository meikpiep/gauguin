package com.holokenmod;

import androidx.annotation.NonNull;

import com.holokenmod.options.GameVariant;

import java.util.ArrayList;
import java.util.Collection;

public class GridCage {
	private final ArrayList<GridCell> mCells;
	private final Grid grid;
	
	private GridCageAction mAction;
	private int mResult;
	private boolean mUserMathCorrect;
	private boolean mSelected;
	private int mId;
	
	public GridCage(final Grid grid) {
		this.grid = grid;
		mUserMathCorrect = true;
		mSelected = false;
		mCells = new ArrayList<>();
	}
	
	public GridCage(Grid grid, GridCageAction action, int result) {
		this(grid);
		
		this.mAction = action;
		this.mResult = result;
	}
	
	public static GridCage createWithCells(Grid grid, GridCell firstCell, int[][] cage_coords) {
		GridCage cage = new GridCage(grid);
		
		for (final int[] cage_coord : cage_coords) {
			final int col = firstCell.getColumn() + cage_coord[0];
			final int row = firstCell.getRow() + cage_coord[1];
			cage.addCell(grid.getCellAt(row, col));
		}
		
		return cage;
	}
	
	public static GridCage createWithCells(Grid grid, Collection<GridCell> cells) {
		GridCage cage = new GridCage(grid);
		
		for (GridCell cell : cells) {
			cage.addCell(grid.getCell(cell.getCellNumber()));
		}
		
		return cage;
	}
	
	@NonNull
	public String toString() {
		String retStr = "";
		retStr += "Cage id: " + this.mId;
		retStr += ", Action: ";
		switch (this.mAction) {
			case ACTION_NONE:
				retStr += "None";
				break;
			case ACTION_ADD:
				retStr += "Add";
				break;
			case ACTION_SUBTRACT:
				retStr += "Subtract";
				break;
			case ACTION_MULTIPLY:
				retStr += "Multiply";
				break;
			case ACTION_DIVIDE:
				retStr += "Divide";
				break;
		}
		
		retStr += ", ActionStr: " + this.mAction
				.getOperationDisplayName() + ", Result: " + this.mResult;
		retStr += ", cells: " + getCellNumbers();
		
		return retStr;
	}
	
	public void setSingleCellArithmetic() {
		this.mAction = GridCageAction.ACTION_NONE;
		this.mResult = this.mCells.get(0).getValue();
	}
	
	public void setCageId(final int id) {
		this.mId = id;
	}
	
	private boolean isAddMathsCorrect() {
		int total = 0;
		for (final GridCell cell : this.mCells) {
			total += cell.getUserValue();
		}
		return (total == this.mResult);
	}
	
	private boolean isMultiplyMathsCorrect() {
		int total = 1;
		for (final GridCell cell : this.mCells) {
			total *= cell.getUserValue();
		}
		return (total == this.mResult);
	}
	
	private boolean isDivideMathsCorrect() {
        if (this.mCells.size() != 2) {
            return false;
        }
        
        if (this.mCells.get(0).getUserValue() > this.mCells.get(1).getUserValue()) {
            return this.mCells.get(0).getUserValue() == (this.mCells.get(1)
                    .getUserValue() * this.mResult);
        } else {
            return this.mCells.get(1).getUserValue() == (this.mCells.get(0)
                    .getUserValue() * this.mResult);
        }
	}
	
	private boolean isSubtractMathsCorrect() {
        if (this.mCells.size() != 2) {
            return false;
        }
        
        if (this.mCells.get(0).getUserValue() > this.mCells.get(1).getUserValue()) {
            return (this.mCells.get(0).getUserValue() - this.mCells.get(1)
                    .getUserValue()) == this.mResult;
        } else {
            return (this.mCells.get(1).getUserValue() - this.mCells.get(0)
                    .getUserValue()) == this.mResult;
        }
	}
	
	public boolean isMathsCorrect() {
        if (this.mCells.size() == 1) {
            return this.mCells.get(0).isUserValueCorrect();
        }
		
		if (GameVariant.getInstance().showOperators()) {
			switch (this.mAction) {
				case ACTION_ADD:
					return isAddMathsCorrect();
				case ACTION_MULTIPLY:
					return isMultiplyMathsCorrect();
				case ACTION_DIVIDE:
					return isDivideMathsCorrect();
				case ACTION_SUBTRACT:
					return isSubtractMathsCorrect();
			}
		} else {
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
        for (final GridCell cell : this.mCells) {
            if (!cell.isUserValueSet()) {
                this.setBorders();
                return;
            }
        }
		this.mUserMathCorrect = this.isMathsCorrect();
		this.setBorders();
	}
	
	public void setBorders() {
		for (final GridCell cell : this.mCells) {
			for (final Direction direction : Direction.values()) {
				cell.getCellBorders().setBorderType(direction, GridBorderType.BORDER_NONE);
			}
            if (this.grid.getCage(cell.getRow() - 1, cell.getColumn()) != this) {
                if (!this.mUserMathCorrect && GameVariant.getInstance().showBadMaths()) {
                    cell.getCellBorders()
                            .setBorderType(Direction.NORTH, GridBorderType.BORDER_WARN);
                } else if (this.mSelected) {
                    cell.getCellBorders()
                            .setBorderType(Direction.NORTH, GridBorderType.BORDER_CAGE_SELECTED);
                } else {
                    cell.getCellBorders()
                            .setBorderType(Direction.NORTH, GridBorderType.BORDER_SOLID);
                }
            }
            
            if (this.grid.getCage(cell.getRow(), cell.getColumn() + 1) != this) {
                if (!this.mUserMathCorrect && GameVariant.getInstance().showBadMaths()) {
                    cell.getCellBorders().setBorderType(Direction.EAST, GridBorderType.BORDER_WARN);
                } else if (this.mSelected) {
                    cell.getCellBorders()
                            .setBorderType(Direction.EAST, GridBorderType.BORDER_CAGE_SELECTED);
                } else {
                    cell.getCellBorders()
                            .setBorderType(Direction.EAST, GridBorderType.BORDER_SOLID);
                }
            }
            
            if (this.grid.getCage(cell.getRow() + 1, cell.getColumn()) != this) {
                if (!this.mUserMathCorrect && GameVariant.getInstance().showBadMaths()) {
                    cell.getCellBorders()
                            .setBorderType(Direction.SOUTH, GridBorderType.BORDER_WARN);
                } else if (this.mSelected) {
                    cell.getCellBorders()
                            .setBorderType(Direction.SOUTH, GridBorderType.BORDER_CAGE_SELECTED);
                } else {
                    cell.getCellBorders()
                            .setBorderType(Direction.SOUTH, GridBorderType.BORDER_SOLID);
                }
            }
            
            if (this.grid.getCage(cell.getRow(), cell.getColumn() - 1) != this) {
                if (!this.mUserMathCorrect && GameVariant.getInstance().showBadMaths()) {
                    cell.getCellBorders().setBorderType(Direction.WEST, GridBorderType.BORDER_WARN);
                } else if (this.mSelected) {
                    cell.getCellBorders()
                            .setBorderType(Direction.WEST, GridBorderType.BORDER_CAGE_SELECTED);
                } else {
                    cell.getCellBorders()
                            .setBorderType(Direction.WEST, GridBorderType.BORDER_SOLID);
                }
            }
		}
	}
	
	public int getId() {
		return mId;
	}
	
	public void addCell(final GridCell cell) {
		this.mCells.add(cell);
		cell.setCage(this);
	}
	
	public String getCellNumbers() {
		final StringBuilder numbers = new StringBuilder();
		
		for (final GridCell cell : this.mCells) {
			numbers.append(cell.getCellNumber()).append(",");
		}
		
		return numbers.toString();
	}
	
	public int getNumberOfCells() {
		return this.mCells.size();
	}
	
	public GridCell getCell(final int cellNumber) {
		return this.mCells.get(cellNumber);
	}
	
	public ArrayList<GridCell> getCells() {
		return this.mCells;
	}
    
    public void updateCageText() {
        if (GameVariant.getInstance().showOperators()) {
            setCagetext(this.mResult + this.mAction.getOperationDisplayName());
        } else {
            setCagetext(this.mResult + "");
        }
    }
    
    private void setCagetext(final String cageText) {
        this.mCells.get(0).setCagetext(cageText);
    }
	
	public void setSelected(final boolean mSelected) {
		this.mSelected = mSelected;
	}
	
	public int getResult() {
		return mResult;
	}
	
	public void setResult(final int mResult) {
		this.mResult = mResult;
	}
	
	public void calculateResultFromAction() {
		if (mAction == GridCageAction.ACTION_ADD) {
			int total = 0;
			for (final GridCell cell : mCells) {
				total += cell.getValue();
			}
			mResult = total;
		}
		if (mAction == GridCageAction.ACTION_MULTIPLY) {
			int total = 1;
			for (final GridCell cell : mCells) {
				total *= cell.getValue();
			}
			mResult = total;
		}
	}
	
	public GridCageAction getAction() {
		return mAction;
	}
	
	public void setAction(final GridCageAction mAction) {
		this.mAction = mAction;
	}
	
	public void addCellNumbers(int... cellNumbers) {
		if (cellNumbers == null) {
			return;
		}
		
		for(int cellNumber : cellNumbers) {
			addCell(grid.getCell(cellNumber));
		}
	}
}