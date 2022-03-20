package com.holokenmod.creation;

import com.holokenmod.Grid;

public interface GridCalculationListener {
	void startingCurrentGridCalculation();
	void startingNextGridCalculation();
	
	void currentGridCalculated(Grid currentGrid);
	void nextGridCalculated(Grid currentGrid);
}
