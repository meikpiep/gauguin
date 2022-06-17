package com.holokenmod;

import androidx.annotation.NonNull;

import com.holokenmod.options.GameVariant;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Grid {
	private final ArrayList<GridCell> cells = new ArrayList<>();
	private final ArrayList<GridCage> cages = new ArrayList<>();
	private final GridSize gridSize;
	private GridCell selectedCell;
	private long playTime;
	private boolean active = false;
	private long creationDate;
	private Collection<Integer> possibleDigits;
	private Collection<Integer> possibleNoneZeroDigits;
	
	public Grid(final GridSize gridSize) {
		this.gridSize = gridSize;
	}
	
	public Grid(GridSize gridSize, long creationDate) {
		this(gridSize);
		
		this.creationDate = creationDate;
	}
	
	public ArrayList<GridCell> getCells() {
		return cells;
	}
	
	public GridSize getGridSize() {
		return gridSize;
	}
	
	public GridCell getSelectedCell() {
		return selectedCell;
	}
	
	public void setSelectedCell(final GridCell SelectedCell) {
		this.selectedCell = SelectedCell;
	}
	
	public GridCage getCage(final int row, final int column) {
        if (row < 0 || row >= gridSize.getHeight() || column < 0 || column >= gridSize.getWidth()) {
            return null;
        }
		return cells.get(column + row * gridSize.getWidth()).getCage();
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
	
	public int getNumberOfMistakes() {
		return (int) cells.stream()
				.filter((cell) -> cell.isUserValueSet() && cell.getUserValue() != cell.getValue())
				.count();
	}
	
	public int getNumberOfFilledCells() {
		return (int) cells.stream()
				.filter(GridCell::isUserValueSet)
				.count();
	}
	
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
	
	public List<GridCell> getPossiblesInRowCol(final GridCell ocell) {
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
	
	public GridCell getCellAt(final int row, final int column) {
		if (!isValidCell(row, column)) {
			return null;
		}
		
		return cells.get(column + row * gridSize.getWidth());
	}
	
	public boolean isValidCell(final int row, final int column) {
		return row >= 0 && row < gridSize.getHeight()
				&& column >= 0 && column < gridSize.getWidth();
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
		
		if (selectedCell != null) {
			selectedCell.setSelected(false);
			selectedCell.getCage().setSelected(false);
		}
	}
	
	public void clearLastModified() {
		for (final GridCell cell : cells) {
			cell.setLastModified(false);
		}
	}
	
	public void solveSelectedCage() {
		if (selectedCell == null) {
			return;
		}
		
		for (final GridCell cell : selectedCell.getCage().getCells()) {
			if (!cell.isUserValueCorrect()) {
				cell.clearPossibles();
				cell.setUserValueIntern(cell.getValue());
				cell.setCheated(true);
			}
		}
		
		selectedCell.setSelected(false);
		selectedCell.getCage().setSelected(false);
	}
	
	public void solveGrid() {
		for (final GridCell cell : cells) {
			if (!cell.isUserValueCorrect()) {
				cell.clearPossibles();
				cell.setUserValueIntern(cell.getValue());
				cell.setCheated(true);
			}
		}
		
		if (selectedCell != null) {
			selectedCell.setSelected(false);
			selectedCell.getCage().setSelected(false);
		}
	}
	
	public Collection<Integer> getPossibleDigits() {
		if (possibleDigits == null) {
			possibleDigits = GameVariant
					.getInstance()
					.getDigitSetting()
					.getPossibleDigits(gridSize);
		}
		
		return possibleDigits;
	}
	
	public int getMaximumDigit() {
		return GameVariant
				.getInstance()
				.getDigitSetting()
				.getMaximumDigit(gridSize);
	}
	
	public Collection<Integer> getPossibleNonZeroDigits() {
		if (possibleNoneZeroDigits == null) {
			possibleNoneZeroDigits = GameVariant
					.getInstance()
					.getDigitSetting()
					.getPossibleNonZeroDigits(gridSize);
		}
		
		return possibleNoneZeroDigits;
	}
	
	public void setPlayTime(long playTime) {
		this.playTime = playTime;
	}
	
	public long getPlayTime() {
		return this.playTime;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public long getCreationDate() {
		return this.creationDate;
	}
	
	@NonNull
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Grid:" + System.lineSeparator());
		
		toStringOfCellValues(builder);
		
		builder.append(System.lineSeparator());
		builder.append(System.lineSeparator());
		
		toStringOfCages(builder);
		
		
		return builder.toString();
	}
	
	public String toStringCellsOnly() {
		StringBuilder builder = new StringBuilder();
		
		toStringOfCellValues(builder);
		
		return builder.toString();
	}
	
	private void toStringOfCellValues(StringBuilder builder) {
		for(GridCell cell : cells) {
			builder.append("| " + StringUtils.leftPad(Integer.toString(cell.getUserValue()), 2) + " ");
			builder.append(StringUtils.leftPad(Integer.toString(cell.getValue()), 2) + " ");
			
			if ((cell.getCellNumber() % gridSize.getWidth()) == gridSize.getWidth() - 1) {
				builder.append("|");
				builder.append(System.lineSeparator());
			}
		}
	}
	
	private void toStringOfCages(StringBuilder builder) {
		for(GridCell cell : cells) {
			builder.append("| ");
			builder.append(StringUtils.leftPad(cell.getCageText(), 6));
			builder.append(" ");
			
			String cageId;
			
			if (cell.getCage() != null) {
				cageId = Integer.toString(cell.getCage().getId());
			} else {
				cageId = "";
			}
			
			builder.append(StringUtils.leftPad(cageId, 2));
			builder.append(" ");
			
			if ((cell.getCellNumber() % gridSize.getWidth()) == gridSize.getWidth() - 1) {
				builder.append("|");
				builder.append(System.lineSeparator());
			}
		}
	}
	
	public void addAllCells() {
		int cellnum = 0;
		
		for (int row = 0; row < gridSize.getHeight(); row++) {
			for (int column = 0; column < gridSize.getWidth(); column++) {
				addCell(new GridCell(cellnum++, row, column));
			}
		}
	}
	
	public boolean isUserValueUsedInSameRow(int cellIndex, int value) {
		final int startIndex = cellIndex - (cellIndex % gridSize.getWidth());
		
		for (int index = startIndex; index < startIndex + gridSize.getWidth(); index++) {
			if (index != cellIndex && cells.get(index).getUserValue() == value) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isUserValueUsedInSameColumn(int cellIndex, int value) {
		for (int index = cellIndex % gridSize.getWidth(); index < gridSize.getSurfaceArea(); index += gridSize.getWidth()) {
			if (index != cellIndex && cells.get(index).getUserValue() == value) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isValueUsedInSameRow(int cellIndex, int value) {
		final int startIndex = cellIndex - (cellIndex % gridSize.getWidth());
		
		for (int index = startIndex; index < startIndex + gridSize.getWidth(); index++) {
			if (index != cellIndex && cells.get(index).getValue() == value) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isValueUsedInSameColumn(int cellIndex, int value) {
		for (int index = cellIndex % gridSize.getWidth(); index < gridSize.getSurfaceArea(); index += gridSize.getWidth()) {
			if (index != cellIndex && cells.get(index).getValue() == value) {
				return true;
			}
		}
		
		return false;
	}
	
	public Grid copyEmpty() {
		Grid grid = new Grid(gridSize);
		
		grid.addAllCells();
		
		int cageId = 0;
		
		for(GridCage cage : cages) {
			GridCage newCage = GridCage.createWithCells(
					grid,
					cage.getCells());
			
			newCage.setCageId(cageId);
			cageId++;
			
			grid.addCage(newCage);
		}
		
		for(GridCell cell : this.cells) {
			grid.getCell(cell.getCellNumber()).setValue(cell.getValue());
		}
		
		return grid;
	}
	
	public void updateBorders() {
		for (final GridCage cage : cages) {
			cage.setBorders();
		}
	}
	
	public void addPossiblesAtNewGame() {
		for (final GridCell cell : cells) {
			addAllPossibles(cell);
		}
	}
	
	private void addAllPossibles(final GridCell cell) {
		for (final int i : getPossibleDigits()) {
			cell.addPossible(i);
		}
	}
}