package com.holokenmod.creation;

import com.holokenmod.Grid;
import com.holokenmod.GridCell;

import java.util.ArrayList;
import java.util.Collections;

public class GridRandomizer {
	private final Grid grid;
	
	public GridRandomizer(Grid grid) {
		this.grid = grid;
	}
	
	public void createGrid() {
		createCells(0);
	}
	
	private boolean createCells(int cellNumber) {
		if (cellNumber == grid.getCells().size()) {
			return true;
		}
		
		GridCell cell = grid.getCell(cellNumber);
		
		ArrayList<Integer> possibleDigits = new ArrayList<>();
		
		for(int digit : grid.getPossibleDigits()) {
			if (!grid.isValueUsedInSameRow(cellNumber, digit)
					&& !grid.isValueUsedInSameColumn(cellNumber, digit)) {
				possibleDigits.add(digit);
			}
		}
		
		Collections.shuffle(possibleDigits);
		
		for(int digit : possibleDigits) {
			cell.setValue(digit);
			
			if (createCells(cellNumber + 1)) {
				return true;
			}
		}
		
		return false;
	}
}