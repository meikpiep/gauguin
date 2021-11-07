package com.holokenmod;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class GridCell {
    // Index of the cell (left to right, top to bottom, zero-indexed)
    private final int mCellNumber;
    private final int mColumn;
    private final int mRow;
    private int mValue;
    private int mUserValue;
    private GridCage cage = null;

    private String cageText;

    private GridCellBorders cellBorders = new GridCellBorders();
    private boolean mCheated;
    private SortedSet<Integer> possibles;

    private boolean mShowWarning;
    private boolean mSelected;
    private boolean mLastModified;
    private boolean mInvalidHighlight;

    public GridCell(final int cellNumber, final int row, final int column) {
        this.mCellNumber = cellNumber;
        this.mRow = row;
        this.mColumn = column;
        this.mValue = -1;
        this.mUserValue = -1;
        this.cageText = "";
        this.mCheated = false;
        this.possibles = Collections.synchronizedSortedSet(new TreeSet<>());
        this.mShowWarning = false;
        this.mLastModified = false;
        this.mInvalidHighlight = false;
    }

    @Override
    public String toString() {
        return "GridCell{" +
                "mColumn=" + mColumn +
                ", mRow=" + mRow +
                ", mValue=" + mValue +
                '}';
    }

    public boolean isUserValueCorrect()  {
        return mUserValue == mValue;
    }

    public boolean isUserValueSet() {
        return mUserValue != -1;
    }

    /* Returns whether the cell is a member of any cage */
    public boolean CellInAnyCage()
    {
        return cage != null;
    }

    void setCage(final GridCage cage) {
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

    public void setCellBorders(final GridCellBorders gridCellBorders) {
        this.cellBorders = gridCellBorders;
    }

    public String getCageText() {
         return this.cageText;
    }

    public void setCagetext(final String cageText) {
        this.cageText = cageText;
    }

    public void setValue(final int value) {
         this.mValue = value;
    }

    public void setUserValueIntern(final int value) {
         this.mUserValue = value;
    }

    public synchronized void setUserValue(final int digit) {
        this.clearPossibles();
        this.setUserValueIntern(digit);
        this.setInvalidHighlight(false);
    }

    public synchronized void clearUserValue() {
        setUserValue(-1);
    }

    public void setCheated(final boolean cheated) {
         this.mCheated = cheated;
    }

    public boolean isCheated() {
         return this.mCheated;
    }

    public void togglePossible(final int digit) {
        if (!isPossible(digit))
            this.possibles.add(digit);
        else
            this.possibles.remove(digit);
    }

    public boolean isPossible(final int digit) {
        return this.possibles.contains(digit);
    }

    public synchronized void removePossible(final int digit) {
        this.possibles.remove(digit);
    }

    public void clearPossibles() {
         this.possibles.clear();
    }

    public SortedSet<Integer> getPossibles() {
         return this.possibles;
    }

    public void addPossible(final int digit) {
         this.possibles.add(digit);
    }

    public void setPossibles(final SortedSet<Integer> possibles) {
         this.possibles = possibles;
    }

    public void setInvalidHighlight(final boolean value) {
         this.mInvalidHighlight = value;
    }
    public boolean isInvalidHighlight() {
        return this.mInvalidHighlight;
    }

    public void setLastModified(final boolean value) {
        this.mLastModified = value;
    }

    public boolean isLastModified() {
         return mLastModified;
    }

    public void setShowWarning(final boolean mShowWarning) {
        this.mShowWarning = mShowWarning;
    }

    public boolean isShowWarning() {
         return mShowWarning;
    }

    public boolean isSelected() {
         return mSelected;
    }

    public void setSelected(final boolean selected) {
         this.mSelected = selected;
    }
}