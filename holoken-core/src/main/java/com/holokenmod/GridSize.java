package com.holokenmod;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class GridSize {
	private final int width;
	private final int height;
	
	public GridSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public static GridSize create(String gridSizeString) {
		if (StringUtils.isNumeric(gridSizeString)) {
			int size = Integer.parseInt(gridSizeString);
			
			return new GridSize(size, size);
		}
		
		String[] parts = StringUtils.split(gridSizeString, "x");
		int width = Integer.parseInt(parts[0]);
		int height = Integer.parseInt(parts[1]);
		
		return new GridSize(width, height);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		
		GridSize gridSize = (GridSize) o;
		
		return new EqualsBuilder().append(width, gridSize.width)
				.append(height, gridSize.height).isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(width).append(height).toHashCode();
	}
	
	public int getSurfaceArea() {
		return width * height;
	}
	
	@NonNull
	@Override
	public String toString() {
		return width + "x" + height;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getAmountOfNumbers() {
		return Math.max(width, height);
	}
	
	public boolean isSquare() {
		return width == height;
	}
}