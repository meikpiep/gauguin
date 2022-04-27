package com.holokenmod.creation;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;

import java.math.BigInteger;

public class GridDifficulty {
	private final Grid grid;
	
	public GridDifficulty(Grid grid) {
		this.grid = grid;
	}
	
	public float calculate() {
		BigInteger difficulty = BigInteger.ONE;
		
		for(GridCage cage : grid.getCages()) {
			GridSingleCageCreator cageCreator = new GridSingleCageCreator(grid, cage);
			
			difficulty = difficulty.multiply(BigInteger.valueOf(cageCreator.getPossibleNums().size()));
		}
		
		System.out.println("difficulty: " + difficulty);
		
		BigInteger diffStepOne = difficulty.divide(BigInteger.valueOf((long) Math.pow(10, 14)));
		BigInteger diffStepTwo = diffStepOne.divide(BigInteger.valueOf((long) Math.pow(10, 14)));
		
		return diffStepTwo.floatValue();
	}
}
