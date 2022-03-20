package com.holokenmod.creation;

import com.holokenmod.Grid;
import com.holokenmod.GridSize;

import java.util.ArrayList;

public class GridCalculationService {
	private static GridCalculationService INSTANCE = new GridCalculationService();
	
	private final ArrayList<GridCalculationListener> listeners = new ArrayList<>();
	
	public static GridCalculationService getInstance() {
		return INSTANCE;
	}
	
	public void addListener(GridCalculationListener listener) {
		listeners.add(listener);
	}
	
	public void calculateCurrentAndNextGrids(GridSize gridSize) {
		calculateCurrentGrid(gridSize);
		calculateNextGrid(gridSize);
	}
	
	private void calculateCurrentGrid(GridSize gridSize) {
		listeners.forEach(GridCalculationListener::startingCurrentGridCalculation);
		
		final GridCreator creator = new GridCreator(gridSize);
		
		Grid newGrid = creator.create();
		newGrid.setActive(true);
		
		listeners.forEach(listener -> listener.currentGridCalculated(newGrid));
	}
	
	private void calculateNextGrid(GridSize gridSize) {
		listeners.forEach(GridCalculationListener::startingNextGridCalculation);
		
		final GridCreator creator = new GridCreator(gridSize);
		
		Grid newGrid = creator.create();
		//newGrid.setActive(true);
		
		listeners.forEach(listener -> listener.nextGridCalculated(newGrid));
	}
}
