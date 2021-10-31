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
    private GridCage cage = null;

    private String cageText;

    private GridCellBorders cellBorders = new GridCellBorders();
    private boolean mCheated;
    private List<Integer> possibles;

    private boolean mShowWarning;
    private boolean mSelected;
    private boolean mLastModified;
    private boolean mInvalidHighlight;

    public GridCell(int cellNumber, int row, int column) {
        this.mCellNumber = cellNumber;
        this.mRow = row;
        this.mColumn = column;
        this.mValue = 0;
        this.mUserValue = 0;
        this.cageText = "";
        this.mCheated = false;
        this.possibles = Collections.synchronizedList( new ArrayList<Integer>());
        this.mShowWarning = false;
        this.mLastModified = false;
        this.mInvalidHighlight = false;
    }

    public boolean isUserValueCorrect()  {
        return mUserValue == mValue;
    }

    public boolean isUserValueSet() {
        return mUserValue != 0;
    }

    /* Returns whether the cell is a member of any cage */
    public boolean CellInAnyCage()
    {
        return cage != null;
    }

    void setCage(GridCage cage) {
        this.cage = cage;
    }

    public GridCage getCage() {
        return this.cage;
    }

    public GridCellBorders getCellBorders() {
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

    public void setUserValueIntern(int value) {
         this.mUserValue = value;
    }

    public synchronized void setUserValue(int digit) {
        this.clearPossibles();
        this.setUserValueIntern(digit);
        this.setInvalidHighlight(false);
    }

    public synchronized void clearUserValue() {
        setUserValue(0);
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

    public void setInvalidHighlight(boolean value) {
         this.mInvalidHighlight = value;
    }
    public boolean isInvalidHighlight() {
        return this.mInvalidHighlight;
    }

    public void setLastModified(boolean value) {
        this.mLastModified = value;
    }

    public boolean isLastModified() {
         return mLastModified;
    }

    public void setShowWarning(boolean mShowWarning) {
        this.mShowWarning = mShowWarning;
    }

    public boolean isShowWarning() {
         return mShowWarning;
    }

    public boolean isSelected() {
         return mSelected;
    }

    public void setSelected(boolean selected) {
         this.mSelected = selected;
    }


}