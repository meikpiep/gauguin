package com.holokenmod.creation;

import com.holokenmod.creation.cage.GridSingleCageCreator;
import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridCage;
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
		
		if (!isGridVariantSupported()) {
			return difficultyAsText;
		}
		
		return difficultyAsText;
	}
	
	public boolean isGridVariantSupported() {
		return grid.getOptions().getDigitSetting() == DigitSetting.FIRST_DIGIT_ONE
				&& grid.getOptions().showOperators()
				&& grid.getOptions().getSingleCageUsage() == SingleCageUsage.FIXED_NUMBER
				&& grid.getOptions().getCageOperation() == GridCageOperation.OPERATIONS_ALL
				&& grid.getGridSize().getHeight() == 9
				&& grid.getGridSize().getWidth() == 9;
	}
	
	public GameDifficulty getDifficulty() {
		return getDifficulty(calculate());
	}
	
	private GameDifficulty getDifficulty(double difficultyValue) {
		
		if (difficultyValue >= 86.51) {
			return GameDifficulty.EXTREME;
		}
		if (difficultyValue >= 80.80) {
			return GameDifficulty.HARD;
		}
		if (difficultyValue >= 76.20) {
			return GameDifficulty.MEDIUM;
		}
		if (difficultyValue >= 70.40) {
			return GameDifficulty.EASY;
		}
		
		return GameDifficulty.VERY_EASY;
	}
}
