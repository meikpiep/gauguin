package com.holokenmod.calculation;

import com.holokenmod.creation.GridCalculator;
import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridSize;
import com.holokenmod.options.GameOptionsVariant;

import java.util.ArrayList;
import java.util.Optional;

public class GridCalculationService {
	private static final GridCalculationService INSTANCE = new GridCalculationService();
	
	private final ArrayList<GridCalculationListener> listeners = new ArrayList<>();
	private Optional<Grid> nextGrid = Optional.empty();
	private GridSize gridSize;
	private GameOptionsVariant gameVariant;
	
	public static GridCalculationService getInstance() {
		return INSTANCE;
	}
	
	public void addListener(GridCalculationListener listener) {
		listeners.add(listener);
	}
	
	public void calculateCurrentAndNextGrids(GridSize gridSize, GameOptionsVariant gameVariant) {
		this.nextGrid = Optional.empty();
		this.gridSize = gridSize;
		this.gameVariant = gameVariant;
		
		calculateCurrentGrid();
		calculateNextGrid();
	}
	
	private void calculateCurrentGrid() {
		listeners.forEach(GridCalculationListener::startingCurrentGridCalculation);
		
		final GridCalculator creator = new GridCalculator(gridSize);
		
		Grid newGrid = creator.calculate();
		
		listeners.forEach(listener -> listener.currentGridCalculated(newGrid));
	}
	
	public void calculateNextGrid() {
		listeners.forEach(GridCalculationListener::startingNextGridCalculation);
		
		final GridCalculator creator = new GridCalculator(gridSize);
		
		Grid grid = creator.calculate();
		
		nextGrid = Optional.of(grid);
		
		listeners.forEach(listener -> listener.nextGridCalculated(grid));
	}
	
	public boolean hasCalculatedNextGrid(GridSize gridSizeParam, GameOptionsVariant gameVariantParam) {
		return nextGrid.isPresent()
			&& gridSizeParam.equals(gridSize)
			&& gameVariantParam.equals(gameVariant);
	}
	
	public Grid consumeNextGrid() {
		Grid grid = nextGrid.get();
		
		nextGrid = Optional.empty();
		
		return grid;
	}
	
	public void setGameParameter(GridSize gridSize, GameOptionsVariant gameVariant) {
		this.gridSize = gridSize;
		this.gameVariant = gameVariant;
	}
	
	public void setNextGrid(Grid grid) {
		nextGrid = Optional.of(grid);
	}
}
