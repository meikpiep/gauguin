package com.holokenmod.creation;

import com.holokenmod.creation.cage.GridSingleCageCreator;
import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridCage;
import com.holokenmod.options.CurrentGameOptionsVariant;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GameDifficulty;
import com.holokenmod.options.GridCageOperation;
import com.holokenmod.options.SingleCageUsage;

import java.math.BigInteger;

public class GridDifficultyCalculator {
	private final Grid grid;
	
	public GridDifficultyCalculator(Grid grid) {
		this.grid = grid;
	}
	
	public double calculate() {
		BigInteger difficulty = BigInteger.ONE;
		
		for(GridCage cage : grid.getCages()) {
			GridSingleCageCreator cageCreator = new GridSingleCageCreator(grid, cage);
			
			difficulty = difficulty.multiply(BigInteger.valueOf(cageCreator.getPossibleNums().size()));
		}
		
		double value = Math.log(difficulty.doubleValue());
		
		System.out.println("difficulty: " + value);
		
		return value;
	}
	
	public String getInfo() {
		double difficultyValue = Math.round(calculate());
		String difficultyAsText = Long.toString(Math.round(difficultyValue));
		
		if (CurrentGameOptionsVariant.getInstance().getDigitSetting() != DigitSetting.FIRST_DIGIT_ONE
			|| !CurrentGameOptionsVariant.getInstance().showOperators()
			|| CurrentGameOptionsVariant.getInstance().getSingleCageUsage() != SingleCageUsage.FIXED_NUMBER
			|| CurrentGameOptionsVariant.getInstance().getCageOperation() != GridCageOperation.OPERATIONS_ALL) {
			return difficultyAsText;
		}
		
		GameDifficulty difficulty = GameDifficulty.VERY_EASY;
		
		if (difficultyValue >= 69.24) {
			difficulty = GameDifficulty.EASY;
		}
		if (difficultyValue >= 75.83) {
			difficulty = GameDifficulty.MEDIUM;
		}
		if (difficultyValue >= 80.08) {
			difficulty = GameDifficulty.HARD;
		}
		if (difficultyValue >= 86.23) {
			difficulty = GameDifficulty.EXTREME;
		}
		
		return difficulty.name() + " - " + difficultyAsText;
	}
}
