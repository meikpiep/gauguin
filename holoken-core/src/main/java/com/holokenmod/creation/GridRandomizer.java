package com.holokenmod.creation;

import androidx.annotation.NonNull;

import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridCell;
import com.holokenmod.RandomSingleton;

import java.util.ArrayList;
import java.util.Collections;

class GridRandomizer {
	private enum FillMode {
		HORIZONTAL,
		VERTICAL
	}
	
	private final Grid grid;
	private final FillMode fillMode;
	
	GridRandomizer(Grid grid) {
		this.grid = grid;
		
		if (grid.getGridSize().getHeight() > grid.getGridSize().getWidth()) {
			this.fillMode = FillMode.VERTICAL;
		} else {
			this.fillMode = FillMode.HORIZONTAL;
		}
	}
	
	void createGrid() {
		createCells(0, 0);
	}
	
	private boolean createCells(int column, int row) {
		if (column == grid.getGridSize().getWidth()
			|| row == grid.getGridSize().getHeight()) {
			return true;
		}
		
		GridCell cell = grid.getCellAt(row, column);
		
		ArrayList<Integer> possibleDigits;
		
		possibleDigits = getShuffledPossibleDigits(column + row * grid.getGridSize().getWidth());
		
		for(int digit : possibleDigits) {
			cell.setValue(digit);
			
			int nextRow = row;
			int nextColumn = column;
			
			if (fillMode == FillMode.HORIZONTAL) {
				nextColumn++;
				
				if (nextColumn == grid.getGridSize().getWidth()) {
					nextColumn = 0;
					nextRow++;
				}
			} else {
				nextRow++;
				
				if (nextRow == grid.getGridSize().getHeight()) {
					nextRow = 0;
					nextColumn++;
				}
			}
			
			if (createCells(nextColumn, nextRow)) {
				return true;
			}
		}
		
		cell.setValue(GridCell.NO_VALUE_SET);
		
		return false;
	}
	
	@NonNull
	private ArrayList<Integer> getShuffledPossibleDigits(int cellNumber) {
		ArrayList<Integer> possibleDigits;
		
		if (cellNumber == 0) {
			possibleDigits = new ArrayList<>(grid.getPossibleDigits());
		} else {
			possibleDigits = new ArrayList<>();
			
			for (int digit : grid.getPossibleDigits()) {
				if (!grid.isValueUsedInSameRow(cellNumber, digit)
						&& !grid.isValueUsedInSameColumn(cellNumber, digit)) {
					possibleDigits.add(digit);
				}
			}
		}
		
		if (!possibleDigits.isEmpty()) {
			Collections.shuffle(possibleDigits, RandomSingleton.getInstance().getRandom());
		}
		
		return possibleDigits;
	}
}