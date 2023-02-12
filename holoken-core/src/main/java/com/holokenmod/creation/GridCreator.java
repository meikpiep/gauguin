package com.holokenmod.creation;

import com.holokenmod.RandomSingleton;
import com.holokenmod.Randomizer;
import com.holokenmod.creation.cage.GridCageCreator;
import com.holokenmod.grid.Grid;
import com.holokenmod.options.DifficultySetting;
import com.holokenmod.options.GameVariant;

public class GridCreator {
	private final GameVariant variant;
	private final Randomizer randomizer;
	private final PossibleDigitsShuffler shuffler;
	
	public GridCreator(final GameVariant variant) {
		this(RandomSingleton.getInstance(), new RandomPossibleDigitsShuffler(), variant);
	}
	
	public GridCreator(final Randomizer randomizer, final PossibleDigitsShuffler shuffler, final GameVariant variant) {
		this.randomizer = randomizer;
		this.shuffler = shuffler;
		this.variant = variant;
	}
	
	public Grid createRandomizedGridWithCages() {
		randomizer.discard();
		
		Grid newGrid;
		
		do {
			newGrid = new Grid(variant);
			
			newGrid.addAllCells();
			
			randomiseGrid(newGrid);
			createCages(newGrid);
		} while (!isWantedDifficulty(newGrid));
		
		return newGrid;
	}
	
	private boolean isWantedDifficulty(Grid grid) {
		if (variant.getOptions().getDifficultySetting() == DifficultySetting.ANY) {
			return true;
		}
		
		GridDifficultyCalculator calculator = new GridDifficultyCalculator(grid);
		
		if (!calculator.isGridVariantSupported()) {
			return true;
		}
		
		return calculator.getDifficulty() == variant.getOptions().getDifficultySetting().getGameDifficulty();
	}
	
	private void createCages(Grid grid) {
		GridCageCreator creator = new GridCageCreator(randomizer, grid);
		
		creator.createCages();
	}
	
	private void randomiseGrid(Grid grid) {
		GridRandomizer randomizer = new GridRandomizer(shuffler, grid);
		
		randomizer.createGrid();
	}
}