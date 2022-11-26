package com.holokenmod.calculation;

import com.holokenmod.grid.Grid;

public interface GridCalculationListener {
	void startingCurrentGridCalculation();
	void startingNextGridCalculation();
	
	void currentGridCalculated(Grid currentGrid);
	void nextGridCalculated(Grid currentGrid);
}
