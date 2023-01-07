package com.holokenmod.options;

import com.holokenmod.grid.GridSize;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class GameVariant {
	private final GridSize gridSize;
	private final GameOptionsVariant optionsVariant;
	
	public GameVariant(GridSize gridSize, GameOptionsVariant optionsVariant) {
		this.gridSize = gridSize;
		this.optionsVariant = optionsVariant;
	}
	
	public GridSize getGridSize() {
		return gridSize;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		
		GameVariant that = (GameVariant) o;
		
		return new EqualsBuilder()
				.append(gridSize, that.gridSize)
				.append(optionsVariant, that.optionsVariant)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(gridSize)
				.append(optionsVariant)
				.toHashCode();
	}
	
	public int getWidth() {
		return gridSize.getWidth();
	}
	
	public int getHeight() {
		return gridSize.getHeight();
	}
	
	public int getSurfaceArea() {
		return gridSize.getSurfaceArea();
	}
	
	public GameOptionsVariant getOptions() {
		return optionsVariant;
	}
}
