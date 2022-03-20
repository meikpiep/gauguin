package com.holokenmod.creation;

import com.holokenmod.Grid;
import com.holokenmod.GridSize;
import com.holokenmod.options.GameVariant;

import java.util.ArrayList;
import java.util.Optional;

public class GridCalculationService {
	private static GridCalculationService INSTANCE = new GridCalculationService();
	
	private final ArrayList<GridCalculationListener> listeners = new ArrayList<>();
	private Optional<Grid> nextGrid = Optional.empty();
	private GridSize gridSize;
	private GameVariant gameVariant;
	
	public static GridCalculationService getInstance() {
		return INSTANCE;
	}
	
	public void addListener(GridCalculationListener listener) {
		listeners.add(listener);
	}
	
	public void calculateCurrentAndNextGrids(GridSize gridSize, GameVariant gameVariant) {
		this.nextGrid = Optional.empty();
		this.gridSize = gridSize;
		this.gameVariant = gameVariant;
		
		calculateCurrentGrid();
		calculateNextGrid();
	}
	
	private void calculateCurrentGrid() {
		listeners.forEach(GridCalculationListener::startingCurrentGridCalculation);
		
		final GridCreator creator = new GridCreator(gridSize);
		
		Grid newGrid = creator.create();
		newGrid.setActive(true);
		
		listeners.forEach(listener -> listener.currentGridCalculated(newGrid));
	}
	
	public void calculateNextGrid() {
		listeners.forEach(GridCalculationListener::startingNextGridCalculation);
		
		final GridCreator creator = new GridCreator(gridSize);
		
		Grid grid = creator.create();
		//newGrid.setActive(true);
		
		nextGrid = Optional.of(grid);
		
		listeners.forEach(listener -> listener.nextGridCalculated(grid));
	}
	
	public boolean hasCalculatedNextGrid(GridSize gridSizeParam, GameVariant gameVariantParam) {
		return nextGrid.isPresent()
			&& gridSizeParam.equals(gridSize)
			&& gameVariantParam.equals(gameVariant);
	}
	
	public Grid consumeNextGrid() {
		Grid grid = nextGrid.get();
		
		nextGrid = Optional.empty();
		
		return grid;
	}
	
	public void setGameParameter(GridSize gridSize, GameVariant gameVariant) {
		this.gridSize = gridSize;
		this.gameVariant = gameVariant;
	}
}
