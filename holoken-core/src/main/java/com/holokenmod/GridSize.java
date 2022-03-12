package com.holokenmod;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

public class GridSize {
	private final int width;
	private final int height;
	
	public GridSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public static GridSize create(String gridSizeString) {
		if (StringUtils.isNumeric(gridSizeString)) {
			int size = Integer.valueOf(gridSizeString);
			
			return new GridSize(size, size);
		}
		
		String[] parts = StringUtils.split(gridSizeString, "x");
		int width = Integer.valueOf(parts[0]);
		int height = Integer.valueOf(parts[1]);
		
		return new GridSize(width, height);
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