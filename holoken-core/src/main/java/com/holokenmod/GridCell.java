package com.holokenmod;

import androidx.annotation.NonNull;

import java.util.SortedSet;
import java.util.TreeSet;

public class GridCell {
	public static final int NO_VALUE_SET = Integer.MAX_VALUE;
	// Index of the cell (left to right, top to bottom, zero-indexed)
	private final int number;
	private final int column;
	private final int row;
	
	private int value;
	private int userValue;
	private GridCage cage = null;
	private String cageText;
	private GridCellBorders cellBorders = new GridCellBorders();
	private boolean cheated;
	private SortedSet<Integer> possibles;
	private boolean showWarning;
	private boolean selected;
	private boolean lastModified;
	private boolean invalidHighlight;
	private boolean flaky = false;
	
	public GridCell(final int cellNumber, final int row, final int column) {
		this.number = cellNumber;
		this.row = row;
		this.column = column;
		this.value = GridCell.NO_VALUE_SET;
		this.userValue = GridCell.NO_VALUE_SET;
		this.cageText = "";
		this.cheated = false;
		this.possibles = new TreeSet<>();
		this.showWarning = false;
		this.lastModified = false;
		this.invalidHighlight = false;
	}
	
	@NonNull
	@Override
	public String toString() {
		return "GridCell{" +
				"mColumn=" + column +
				", mRow=" + row +
				", mValue=" + value +
				'}';
	}
	
	public boolean isUserValueCorrect() {
		return userValue == value;
	}
	
	public boolean isUserValueSet() {
		return userValue != NO_VALUE_SET;
	}
	
	/* Returns whether the cell is a member of any cage */
	public boolean CellInAnyCage() {
		return cage != null;
	}
	
	public GridCage getCage() {
		return this.cage;
	}
	
	public void setCage(final GridCage cage) {
		this.cage = cage;
	}
	
	public GridCellBorders getCellBorders() {
		return cellBorders;
	}
	
	public void setCellBorders(final GridCellBorders gridCellBorders) {
		this.cellBorders = gridCellBorders;
	}
	
	public int getCellNumber() {
		return number;
	}
	
	public int getColumn() {
		return column;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(final int value) {
		this.value = value;
	}
	
	public int getUserValue() {
		return userValue;
	}
	
	public synchronized void setUserValue(final int digit) {
		this.clearPossibles();
		this.setUserValueIntern(digit);
		this.setInvalidHighlight(false);
	}
	
	public String getCageText() {
		return this.cageText;
	}
	
	public void setCagetext(final String cageText) {
		this.cageText = cageText;
	}
	
	public void setUserValueIntern(final int value) {
		this.userValue = value;
	}
	
	public synchronized void clearUserValue() {
		setUserValue(GridCell.NO_VALUE_SET);
	}
	
	public boolean isCheated() {
		return this.cheated;
	}
	
	public void setCheated(final boolean cheated) {
		this.cheated = cheated;
	}
	
	public void togglePossible(final int digit) {
        if (!isPossible(digit)) {
            this.possibles.add(digit);
        } else {
            this.possibles.remove(digit);
        }
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
	
	public void setPossibles(final SortedSet<Integer> possibles) {
		this.possibles = possibles;
	}
	
	public void addPossible(final int digit) {
		this.possibles.add(digit);
	}
	
	public boolean isInvalidHighlight() {
		return this.invalidHighlight;
	}
	
	public void setInvalidHighlight(final boolean value) {
		this.invalidHighlight = value;
	}
	
	public boolean isLastModified() {
		return lastModified;
	}
	
	public void setLastModified(final boolean value) {
		this.lastModified = value;
	}
	
	public boolean isShowWarning() {
		return showWarning;
	}
	
	public void setShowWarning(final boolean mShowWarning) {
		this.showWarning = mShowWarning;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(final boolean selected) {
		this.selected = selected;
	}
	
	public boolean isFlaky() {
		return flaky;
	}
	
	public void toogleFlaky() {
		flaky = !flaky;
	}
	
	public void setFlaky(boolean flaky) {
		this.flaky = flaky;
	}
}