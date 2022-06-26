package com.holokenmod.calculation;

import com.holokenmod.Grid;

public interface GridCalculationListener {
	void startingCurrentGridCalculation();
	void startingNextGridCalculation();
	
	void currentGridCalculated(Grid currentGrid);
	void nextGridCalculated(Grid currentGrid);
}
