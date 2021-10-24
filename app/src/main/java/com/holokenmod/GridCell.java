package com.holokenmod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GridCell {
    // Index of the cell (left to right, top to bottom, zero-indexed)
    private final int mCellNumber;
    // X grid position, zero indexed
    private final int mColumn;
    // Y grid position, zero indexed
    private final int mRow;
    // Value of the digit in the cell
    private int mValue;
    // User's entered value
    private int mUserValue;
    // Id of the enclosing cage
    private int mCageId;

    private String cageText;

    private GridCellBorders cellBorders;
    private boolean mCheated;
    private List<Integer> possibles;

    public GridCell(int cellNumber, int gridSize) {
        this(cellNumber,
                gridSize,
                cellNumber / gridSize,
                cellNumber % gridSize);
    }

    public GridCell(int cellNumber, int gridSize, int row, int column) {
        this.mCellNumber = cellNumber;
        this.mRow = row;
        this.mColumn = column;
        this.mCageId = -1;
        this.mValue = 0;
        this.mUserValue = 0;
        this.cageText = "";
        this.mCheated = false;
        this.possibles = Collections.synchronizedList( new ArrayList<Integer>());
    }

    boolean isUserValueCorrect()  {
        return mUserValue == mValue;
    }

     boolean isUserValueSet() {
        return mUserValue != 0;
    }

    /* Returns whether the cell is a member of any cage */
     boolean CellInAnyCage()
    {
        return mCageId != -1;
    }

    void setCageId(int id) {
        this.mCageId = id;
    }

    int getCageId() {
        return this.mCageId;
    }

    GridCellBorders getCellBorders() {
        return cellBorders;
    }

    public int getCellNumber() {
        return mCellNumber;
    }

    public int getColumn() {
        return mColumn;
    }

    public int getRow() {
        return mRow;
    }

    public int getValue() {
        return mValue;
    }

    public int getUserValue() {
        return mUserValue;
    }

    public void setCellBorders(GridCellBorders gridCellBorders) {
        this.cellBorders = gridCellBorders;
    }

    public String getCageText() {
         return this.cageText;
    }

    public void setCagetext(String cageText) {
        this.cageText = cageText;
    }

    public void setValue(int value) {
         this.mValue = value;
    }

    public void setUserValue(int value) {
         this.mUserValue = value;
    }

    public void setCheated(boolean cheated) {
         this.mCheated = cheated;
    }

    public boolean isCheated() {
         return this.mCheated;
    }

    public void togglePossible(int digit) {
        if (this.possibles.indexOf(Integer.valueOf(digit)) == -1)
            this.possibles.add(digit);
        else
            this.possibles.remove(Integer.valueOf(digit));
        Collections.sort(possibles);
    }

    public boolean isPossible(int digit) {
        return this.possibles.indexOf(Integer.valueOf(digit)) != -1;
    }

    public synchronized void removePossible(int digit) {
        if (this.possibles.indexOf(Integer.valueOf(digit)) != -1)
            this.possibles.remove(Integer.valueOf(digit));
        Collections.sort(possibles);
    }

    public void clearPossibles() {
         this.possibles.clear();
    }

    public List<Integer> getPossibles() {
         return this.possibles;
    }

    public void addPossible(int digit) {
         this.possibles.add(digit);
    }

    public void setPossibles(List<Integer> possibles) {
         this.possibles = possibles;
    }
}