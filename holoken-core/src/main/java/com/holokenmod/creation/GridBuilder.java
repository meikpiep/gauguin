package com.holokenmod.creation;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCageAction;
import com.holokenmod.GridSize;

public class GridBuilder {
	private final Grid grid;
	private int cageId = 0;
	
	public GridBuilder(int size) {
		this(size, size);
	}
	
	public GridBuilder(int width, int heigth) {
		grid = new Grid(new GridSize(width, heigth));
		
		grid.addAllCells();
	}
	
	public GridBuilder addCage(int result, GridCageAction action, int... cellIds) {
		if ( cellIds == null || cellIds.length == 0) {
			throw new RuntimeException("No cell ids given.");
		}
		
		GridCage cage = new GridCage(grid);
		
		cage.setCageId(cageId++);
		cage.setAction(action);
		cage.setResult(result);
		
		for(int cellId : cellIds) {
			cage.addCell(grid.getCell(cellId));
		}
		
		grid.addCage(cage);

		return this;
	}
	
	public Grid createGrid() {
		grid.setCageTexts();
		
		return grid;
	}
}
