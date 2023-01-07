package com.holokenmod.calculation;

import com.holokenmod.creation.GridCalculator;
import com.holokenmod.grid.Grid;
import com.holokenmod.options.GameVariant;

import java.util.ArrayList;
import java.util.Optional;

public class GridCalculationService {
	private static final GridCalculationService INSTANCE = new GridCalculationService();
	
	private final ArrayList<GridCalculationListener> listeners = new ArrayList<>();
	private Optional<Grid> nextGrid = Optional.empty();
	private GameVariant variant;
	
	public static GridCalculationService getInstance() {
		return INSTANCE;
	}
	
	public void addListener(GridCalculationListener listener) {
		listeners.add(listener);
	}
	
	public void calculateCurrentAndNextGrids(GameVariant variant) {
		this.nextGrid = Optional.empty();
		this.variant = variant;
		
		calculateCurrentGrid();
		calculateNextGrid();
	}
	
	private void calculateCurrentGrid() {
		listeners.forEach(GridCalculationListener::startingCurrentGridCalculation);
		
		final GridCalculator creator = new GridCalculator(variant);
		
		Grid newGrid = creator.calculate();
		
		listeners.forEach(listener -> listener.currentGridCalculated(newGrid));
	}
	
	public void calculateNextGrid() {
		listeners.forEach(GridCalculationListener::startingNextGridCalculation);
		
		final GridCalculator creator = new GridCalculator(variant);
		
		Grid grid = creator.calculate();
		
		nextGrid = Optional.of(grid);
		
		listeners.forEach(listener -> listener.nextGridCalculated(grid));
	}
	
	public boolean hasCalculatedNextGrid(GameVariant variantParam) {
		return nextGrid.isPresent()
			&& variantParam.equals(variant);
	}
	
	public Grid consumeNextGrid() {
		Grid grid = nextGrid.get();
		
		nextGrid = Optional.empty();
		
		return grid;
	}
	
	public void setVariant(GameVariant variant) {
		this.variant = variant;
	}
	
	public void setNextGrid(Grid grid) {
		nextGrid = Optional.of(grid);
	}
}
