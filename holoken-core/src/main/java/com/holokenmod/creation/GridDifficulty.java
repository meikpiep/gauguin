package com.holokenmod.creation;

import com.holokenmod.creation.cage.GridSingleCageCreator;
import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridCage;
import com.holokenmod.options.CurrentGameOptionsVariant;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GridCageOperation;
import com.holokenmod.options.SingleCageUsage;

import java.math.BigInteger;

public class GridDifficulty {
	private final Grid grid;
	
	public GridDifficulty(Grid grid) {
		this.grid = grid;
	}
	
	public BigInteger calculate() {
		BigInteger difficulty = BigInteger.ONE;
		
		for(GridCage cage : grid.getCages()) {
			GridSingleCageCreator cageCreator = new GridSingleCageCreator(grid, cage);
			
			difficulty = difficulty.multiply(BigInteger.valueOf(cageCreator.getPossibleNums().size()));
		}
		
		System.out.println("difficulty: " + difficulty);
		
		BigInteger diffStepOne = difficulty.divide(BigInteger.valueOf((long) Math.pow(10, 14)));
		BigInteger diffStepTwo = diffStepOne.divide(BigInteger.valueOf((long) Math.pow(10, 14)));
		
		return diffStepTwo;
	}
	
	public String getInfo() {
		if (CurrentGameOptionsVariant.getInstance().getDigitSetting() != DigitSetting.FIRST_DIGIT_ONE
			|| !CurrentGameOptionsVariant.getInstance().showOperators()
			|| CurrentGameOptionsVariant.getInstance().getSingleCageUsage() != SingleCageUsage.FIXED_NUMBER
			|| CurrentGameOptionsVariant.getInstance().getCageOperation() != GridCageOperation.OPERATIONS_ALL) {
			return Float.toString(calculate().floatValue());
		}
		
		String level = "Easy";
		
		float difficulty = calculate().floatValue();
		
		if (difficulty >= 1901) {
			level = "Medium";
		}
		if (difficulty >= 128379) {
			level = "Hard";
		}
		
		return level + " - " + difficulty;
	}
}
