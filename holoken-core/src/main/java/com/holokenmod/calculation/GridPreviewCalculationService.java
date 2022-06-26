package com.holokenmod.calculation;

import com.holokenmod.Grid;
import com.holokenmod.creation.GridCreator;
import com.holokenmod.options.GameVariant;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GridPreviewCalculationService {
	
	private final Map<GameVariant, Grid> grids = new HashMap<>();
	
	public GridPreviewCalculationService() {
	
	}
	
	public Grid getOrCreateGrid(GameVariant variant) {
		return grids.computeIfAbsent(variant, computeVariant());
	}
	
	private Function<GameVariant, Grid> computeVariant() {
		return (variant) -> {
			final GridCreator creator = new GridCreator(variant.getGridSize());
			
			return creator.create();
		};
	}
	
	public Grid getGrid(GameVariant gameVariant) {
		return grids.get(gameVariant);
	}
}
