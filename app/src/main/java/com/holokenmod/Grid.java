package com.holokenmod;

import com.holokenmod.options.ApplicationPreferences;

import java.util.ArrayList;
import java.util.Collection;

public class Grid {
	private final ArrayList<GridCell> cells = new ArrayList<>();
	private final ArrayList<GridCage> cages = new ArrayList<>();
	private final int mGridSize;
	private GridCell mSelectedCell;
	
	public Grid(final int gridSize) {
		this.mGridSize = gridSize;
	}
	
	public ArrayList<GridCell> getCells() {
		return cells;
	}
	
	public int getGridSize() {
		return mGridSize;
	}
	
	public GridCell getSelectedCell() {
		return mSelectedCell;
	}
	
	public void setSelectedCell(final GridCell SelectedCell) {
		this.mSelectedCell = SelectedCell;
	}
	
	public GridCage getCage(final int row, final int column) {
        if (row < 0 || row >= mGridSize || column < 0 || column >= mGridSize) {
            return null;
        }
		return cells.get(column + row * mGridSize).getCage();
	}
	
	public ArrayList<GridCell> invalidsHighlighted() {
		final ArrayList<GridCell> invalids = new ArrayList<>();
		for (final GridCell cell : cells) {
			if (cell.isInvalidHighlight()) {
				invalids.add(cell);
			}
		}
		
		return invalids;
	}
	
	public ArrayList<GridCell> cheatedHighlighted() {
		final ArrayList<GridCell> cheats = new ArrayList<>();
        for (final GridCell cell : cells) {
            if (cell.isCheated()) {
                cheats.add(cell);
            }
        }
		
		return cheats;
	}
	
	public void markInvalidChoices() {
		for (final GridCell cell : cells) {
			if (cell.isUserValueSet() && cell.getUserValue() != cell.getValue()) {
				cell.setInvalidHighlight(true);
			}
		}
	}
	
	// Returns whether the puzzle is solved.
	public boolean isSolved() {
        for (final GridCell cell : cells) {
            if (!cell.isUserValueCorrect()) {
                return false;
            }
        }
		return true;
	}
	
	public int countCheated() {
		int counter = 0;
        for (final GridCell cell : cells) {
            if (cell.isCheated()) {
                counter++;
            }
        }
		return counter;
	}
	
	public int[] countMistakes() {
		final int[] counter = {0, 0};
		for (final GridCell cell : cells) {
			if (cell.isUserValueSet()) {
				counter[1]++;
                if (cell.getUserValue() != cell.getValue()) {
                    counter[0]++;
                }
			}
		}
		return counter;
	}
	
	/* Clear any cells containing the given number. */
	public void clearValue(final int value) {
        for (final GridCell cell : cells) {
            if (cell.getValue() == value) {
                cell.setValue(-1);
            }
        }
	}
	
	/* Determine if the given value is in the given column */
	public boolean valueInColumn(final int column, final int value) {
        for (int row = 0; row < mGridSize; row++) {
            if (cells.get(column + row * mGridSize).getValue() == value) {
                return true;
            }
        }
		return false;
	}
	
	// Return the number of times a given user value is in a row
	public int getNumValueInRow(final GridCell ocell) {
		int count = 0;
        for (final GridCell cell : cells) {
            if (cell.getRow() == ocell.getRow() &&
                    cell.getUserValue() == ocell.getUserValue()) {
                count++;
            }
        }
		return count;
	}
	
	// Return the number of times a given user value is in a column
	public int getNumValueInCol(final GridCell ocell) {
		int count = 0;
        
        for (final GridCell cell : cells) {
            if (cell.getColumn() == ocell.getColumn() &&
                    cell.getUserValue() == ocell.getUserValue()) {
                count++;
            }
        }
		return count;
	}
	
	// Return the cells with same possibles in row and column
	public ArrayList<GridCell> getPossiblesInRowCol(final GridCell ocell) {
		final ArrayList<GridCell> possiblesRowCol = new ArrayList<>();
		final int userValue = ocell.getUserValue();
        for (final GridCell cell : cells) {
            if (cell.isPossible(userValue)) {
                if (cell.getRow() == ocell.getRow() || cell.getColumn() == ocell.getColumn()) {
                    possiblesRowCol.add(cell);
                }
            }
        }
		return possiblesRowCol;
	}
	
	public ArrayList<GridCell> getSinglePossibles() {
		final ArrayList<GridCell> singlePossibles = new ArrayList<>();
        for (final GridCell cell : cells) {
            if (cell.getPossibles().size() == 1) {
                singlePossibles.add(cell);
            }
        }
		return singlePossibles;
	}
	
	public GridCell getCellAt(final int row, final int column) {
		if (!isValidCell(row, column)) {
			return null;
		}
		
		return cells.get(column + row * mGridSize);
	}
	
	public boolean isValidCell(final int row, final int column) {
		return row >= 0 && row < mGridSize
				&& column >= 0 && column < mGridSize;
	}
	
	public ArrayList<GridCage> getCages() {
		return cages;
	}
	
	public void ClearAllCages() {
		for (final GridCell cell : this.cells) {
			cell.setCage(null);
			cell.setCagetext("");
		}
		this.cages.clear();
	}
	
	public void setCageTexts() {
		for (final GridCage cage : cages) {
		    cage.updateCageText();
		}
	}
	
	public void addCell(final GridCell cell) {
		this.cells.add(cell);
	}
	
	public GridCell getCell(final int index) {
		return this.cells.get(index);
	}
	
	public void addCage(final GridCage cage) {
		this.cages.add(cage);
	}
	
	public void clearUserValues() {
		for (final GridCell cell : cells) {
			cell.clearUserValue();
			cell.setCheated(false);
		}
		
		if (mSelectedCell != null) {
			mSelectedCell.setSelected(false);
			mSelectedCell.getCage().setSelected(false);
		}
	}
	
	public void clearLastModified() {
		for (final GridCell cell : cells) {
			cell.setLastModified(false);
		}
	}
	
	public void solve(final boolean solveGrid) {
		if (mSelectedCell != null) {
			ArrayList<GridCell> solvecell = mSelectedCell.getCage().getCells();
			if (solveGrid) {
				solvecell = new ArrayList<>(cells);
			}
			
			for (final GridCell cell : solvecell) {
				if (!cell.isUserValueCorrect()) {
					cell.setUserValueIntern(cell.getValue());
					cell.setCheated(true);
				}
			}
			mSelectedCell.setSelected(false);
			mSelectedCell.getCage().setSelected(false);
		}
	}
	
	public Collection<Integer> getPossibleDigits() {
		return ApplicationPreferences
				.getInstance()
				.getDigitSetting()
				.getPossibleDigits(mGridSize);
	}
	
	public int getMaximumDigit() {
		return ApplicationPreferences
				.getInstance()
				.getDigitSetting()
				.getMaximumDigit(mGridSize);
	}
	
	public Collection<Integer> getPossibleNonZeroDigits() {
		return ApplicationPreferences
				.getInstance()
				.getDigitSetting()
				.getPossibleNonZeroDigits(mGridSize);
	}
}