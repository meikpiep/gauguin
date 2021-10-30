package com.holokenmod;

import java.util.ArrayList;

public class Grid {
    private final ArrayList<GridCell> cells;
    private ArrayList<GridCage> cages = new ArrayList<>();
    private int mGridSize;

    public Grid(ArrayList<GridCell> cells, int gridSize) {
        this.mGridSize = gridSize;
        this.cells = cells;
    }

    public ArrayList<GridCell> getCells() {
        return cells;
    }

    public int getGridSize() {
        return mGridSize;
    }

    public void setGridSize(int gridSize) {
        this.mGridSize = gridSize;
    }

    // Returns cage id of cell at row, column
    // Returns -1 if not a valid cell or cage
    public GridCage CageIdAt(int row, int column) {
        if (row < 0 || row >= mGridSize || column < 0 || column >= mGridSize)
            return null;
        return cells.get(column + row*mGridSize).getCage();
    }

    public ArrayList<GridCell> invalidsHighlighted()
    {
        ArrayList<GridCell> invalids = new ArrayList<>();
        for (GridCell cell : cells) {
            if (cell.isInvalidHighlight()) {
                invalids.add(cell);
            }
        }

        return invalids;
    }

    public ArrayList<GridCell> cheatedHighlighted()
    {
        ArrayList<GridCell> cheats = new ArrayList<>();
        for (GridCell cell : cells)
            if (cell.isCheated())
                cheats.add(cell);

        return cheats;
    }

    public boolean markInvalidChoices() {
        boolean isValid = true;

        for (GridCell cell : cells) {
            if (cell.isUserValueSet() && cell.getUserValue() != cell.getValue()) {
                cell.setInvalidHighlight(true);
                isValid = false;
            }
        }

        return isValid;
    }

    // Returns whether the puzzle is solved.
    public boolean isSolved() {
        for (GridCell cell : cells)
            if (!cell.isUserValueCorrect())
                return false;
        return true;
    }

    // Returns whether the puzzle used cheats.
    public int countCheated() {
        int counter = 0;
        for (GridCell cell : cells)
            if (cell.isCheated())
                counter++;
        return counter;
    }

    // Checks whether the user has made any mistakes
    public int[] countMistakes() {
        int counter[] = {0,0};
        for (GridCell cell : cells) {
            if (cell.isUserValueSet()) {
                counter[1]++;
                if (cell.getUserValue() != cell.getValue())
                    counter[0]++;
            }
        }
        return counter;
    }

    /* Clear any cells containing the given number. */
    public void clearValue(int value) {
        for (GridCell cell : cells)
            if (cell.getValue() == value)
                cell.setValue(0);
    }

    /* Determine if the given value is in the given row */
    public boolean valueInRow(int row, int value) {
        for (GridCell cell : cells)
            if (cell.getRow() == row && cell.getValue() == value)
                return true;
        return false;
    }

    /* Determine if the given value is in the given column */
    public boolean valueInColumn(int column, int value) {
        for (int row=0; row< mGridSize; row++)
            if (cells.get(column+row*mGridSize).getValue() == value)
                return true;
        return false;
    }

    // Return the number of times a given user value is in a row
    public int getNumValueInRow(GridCell ocell) {
        int count = 0;
        for (GridCell cell : cells)
            if (cell.getRow() == ocell.getRow() &&
                    cell.getUserValue() == ocell.getUserValue())
                count++;
        return count;
    }

    // Return the number of times a given user value is in a column
    public int getNumValueInCol(GridCell ocell) {
        int count = 0;

        for (GridCell cell : cells)
            if (cell.getColumn() == ocell.getColumn() &&
                    cell.getUserValue() == ocell.getUserValue())
                count++;
        return count;
    }

    // Return the cells with same possibles in row and column
    public ArrayList<GridCell> getPossiblesInRowCol(GridCell ocell) {
        ArrayList<GridCell> possiblesRowCol = new ArrayList<>();
        int userValue = ocell.getUserValue();
        for (GridCell cell : cells)
            if (cell.isPossible(userValue))
                if (cell.getRow() == ocell.getRow() || cell.getColumn() == ocell.getColumn())
                    possiblesRowCol.add(cell);
        return possiblesRowCol;
    }

    // Return the cells with same possibles in row and column
    public ArrayList<GridCell> getSinglePossibles() {
        ArrayList<GridCell> singlePossibles = new ArrayList<>();
        for (GridCell cell : cells)
            if (cell.getPossibles().size() == 1)
                singlePossibles.add(cell);
        return singlePossibles;
    }

    public GridCell getCellAt(int row, int column) {
        if (row < 0 || row >= mGridSize)
            return null;
        if (column < 0 || column >= mGridSize)
            return null;

        return cells.get(column + row*mGridSize);
    }

    public int CreateSingleCages(int operationSet) {
        int singles = mGridSize / 2;
        boolean RowUsed[] = new boolean[mGridSize];
        boolean ColUsed[] = new boolean[mGridSize];
        boolean ValUsed[] = new boolean[mGridSize];
        for (int i = 0 ; i < singles ; i++) {
            GridCell cell;
            while (true) {
                cell = cells.get(RandomSingleton.getInstance().nextInt(mGridSize * mGridSize));
                if (!RowUsed[cell.getRow()] && !ColUsed[cell.getRow()] && !ValUsed[cell.getValue()-1])
                    break;
            }
            ColUsed[cell.getColumn()] = true;
            RowUsed[cell.getRow()] = true;
            ValUsed[cell.getValue()-1] = true;
            GridCage cage = new GridCage(this, GridCage.CAGE_1);
            cage.mCells.add(cell);
            cage.setArithmetic(operationSet);
            cage.setCageId(i);
            cages.add(cage);
        }
        return singles;
    }

    public ArrayList<GridCage> getCages() {
        return cages;
    }

    public void ClearAllCages() {
        for (GridCell cell : this.cells) {
            cell.setCage(null);
            cell.setCagetext("");
        }
        this.cages = new ArrayList<GridCage>();
    }
}
