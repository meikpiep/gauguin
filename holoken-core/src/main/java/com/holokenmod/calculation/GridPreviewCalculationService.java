package com.holokenmod.calculation;

import com.holokenmod.grid.Grid;
import com.holokenmod.creation.GridCreator;
import com.holokenmod.options.GameVariant;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.Function;

public class GridPreviewCalculationService {
	
	private final Map<GameVariant, Grid> grids = new HashMap<>();
	
	public GridPreviewCalculationService() {
	
	}
	
	public Future<Grid> getOrCreateGrid(GameVariant variant) {
		FutureTask<Grid> future = new FutureTask<>(() -> grids.computeIfAbsent(variant, computeVariant()));
		
		new Thread(future).start();
		
		return future;
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
