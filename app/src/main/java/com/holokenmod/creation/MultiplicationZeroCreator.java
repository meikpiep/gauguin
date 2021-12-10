package com.holokenmod.creation;

import com.holokenmod.Grid;

import java.util.ArrayList;

class MultiplicationZeroCreator {
	private final int n_cells;
	private final Grid grid;
	private final GridCageCreator cageCreator;
	
	private final int[] numbers;
	private final ArrayList<int[]> result_set = new ArrayList<>();
	
	MultiplicationZeroCreator(final GridCageCreator cageCreator, final Grid grid, final int n_cells) {
		this.cageCreator = cageCreator;
		this.grid = grid;
		this.n_cells = n_cells;
		this.numbers = new int[n_cells];
	}
	
	ArrayList<int[]> create() {
		getmultcombos(false, n_cells);
		
		return result_set;
	}
	
	private void getmultcombos(final boolean zeroPresent, final int numberOfCells) {
		//Log.d("ZeroCreator", zeroPresent + " - " + n_cells);
		
		if (numberOfCells == 1 && !zeroPresent) {
			numbers[0] = 0;
			if (cageCreator.satisfiesConstraints(numbers)) {
				result_set.add(numbers.clone());
			}
			
			return;
		}
		
		for (final int n : grid.getPossibleDigits()) {
			numbers[numberOfCells - 1] = n;
			
			if (numberOfCells == 1) {
				if (cageCreator.satisfiesConstraints(numbers)) {
					result_set.add(numbers.clone());
				}
			} else {
				if (n == 0) {
					getmultcombos(true, numberOfCells - 1);
				} else {
					getmultcombos(zeroPresent, numberOfCells - 1);
				}
			}
		}
	}
}