package com.holokenmod.creation;

import com.holokenmod.Grid;

import java.util.ArrayList;

class MultiplicationZeroCreator {
	private final int numberOfCells;
	private final Grid grid;
	private final GridCageCreator cageCreator;
	
	private final int[] numbers;
	private final ArrayList<int[]> combinations = new ArrayList<>();
	
	MultiplicationZeroCreator(final GridCageCreator cageCreator, final Grid grid, final int numberOfCells) {
		this.cageCreator = cageCreator;
		this.grid = grid;
		this.numberOfCells = numberOfCells;
		this.numbers = new int[numberOfCells];
	}
	
	ArrayList<int[]> create() {
		fillCombinations(false, numberOfCells);
		
		return combinations;
	}
	
	private void fillCombinations(final boolean zeroPresent, final int numberOfCells) {
		//Log.d("ZeroCreator", zeroPresent + " - " + n_cells);
		
		if (numberOfCells == 1 && !zeroPresent) {
			numbers[0] = 0;
			if (cageCreator.satisfiesConstraints(numbers)) {
				combinations.add(numbers.clone());
			}
			
			return;
		}
		
		for (final int n : grid.getPossibleDigits()) {
			numbers[numberOfCells - 1] = n;
			
			if (numberOfCells == 1) {
				if (cageCreator.satisfiesConstraints(numbers)) {
					combinations.add(numbers.clone());
				}
			} else {
				if (n == 0) {
					fillCombinations(true, numberOfCells - 1);
				} else {
					fillCombinations(zeroPresent, numberOfCells - 1);
				}
			}
		}
	}
}