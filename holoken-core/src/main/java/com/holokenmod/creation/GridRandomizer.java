package com.holokenmod.creation;

import androidx.annotation.NonNull;

import com.holokenmod.Grid;
import com.holokenmod.GridCell;
import com.holokenmod.RandomSingleton;

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
		
		ArrayList<Integer> possibleDigits;
		
		possibleDigits = getShuffledPossibleDigits(cellNumber);
		
		for(int digit : possibleDigits) {
			cell.setValue(digit);
			
			if (createCells(cellNumber + 1)) {
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
		
		Collections.shuffle(possibleDigits, RandomSingleton.getInstance().getRandom());
		
		return possibleDigits;
	}
}