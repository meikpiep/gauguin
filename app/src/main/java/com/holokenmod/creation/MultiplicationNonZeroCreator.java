package com.holokenmod.creation;

import com.holokenmod.Grid;

import java.util.ArrayList;

public class MultiplicationNonZeroCreator {
	private final int targetValue;
	private final int numberOfCells;
	private final Grid grid;
	private final GridCageCreator cageCreator;
	
	private final int[] numbers;
	private final ArrayList<int[]> combinations = new ArrayList<>();
	
	public MultiplicationNonZeroCreator(final GridCageCreator cageCreator, final Grid grid, final int targetValue, final int numberOfCells) {
		this.cageCreator = cageCreator;
		this.grid = grid;
		this.targetValue = targetValue;
		this.numberOfCells = numberOfCells;
		this.numbers = new int[numberOfCells];
	}
	
	public ArrayList<int[]> create() {
		fillCombinations(targetValue, numberOfCells);
		
		return combinations;
	}
	
	private void fillCombinations(final int targetValue, final int numberOfCells) {
		for (final int n : grid.getPossibleNonZeroDigits()) {
			if (targetValue % n != 0) {
				continue;
			}
			
			if (numberOfCells == 1) {
				if (n == targetValue) {
					numbers[0] = n;
					if (cageCreator.satisfiesConstraints(numbers)) {
						combinations.add(numbers.clone());
					}
				}
			} else {
				numbers[numberOfCells - 1] = n;
				
				fillCombinations(targetValue / n, numberOfCells - 1);
			}
		}
	}
}