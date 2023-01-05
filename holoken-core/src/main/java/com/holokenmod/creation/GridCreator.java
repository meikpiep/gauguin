package com.holokenmod.creation;

import com.holokenmod.RandomSingleton;
import com.holokenmod.Randomizer;
import com.holokenmod.creation.cage.GridCageCreator;
import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridSize;

public class GridCreator {
	private final GridSize gridSize;
	private final Randomizer randomizer;
	private final PossibleDigitsShuffler shuffler;
	
	public GridCreator(final GridSize gridSize) {
		this(RandomSingleton.getInstance(), new RandomPossibleDigitsShuffler(), gridSize);
	}
	
	public GridCreator(final Randomizer randomizer, final PossibleDigitsShuffler shuffler, final GridSize gridSize) {
		this.randomizer = randomizer;
		this.shuffler = shuffler;
		this.gridSize = gridSize;
	}
	
	public Grid createRandomizedGridWithCages() {
		randomizer.discard();
		
		Grid newGrid = new Grid(gridSize);
		
		newGrid.addAllCells();
		
		randomiseGrid(newGrid);
		createCages(newGrid);
		
		return newGrid;
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