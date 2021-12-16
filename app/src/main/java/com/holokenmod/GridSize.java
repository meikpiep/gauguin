package com.holokenmod;

import androidx.annotation.NonNull;

public class GridSize {
	private final int width;
	private final int height;
	
	public GridSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public static GridSize create(String gridSizeString) {
		return new GridSize(9, 6);
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
}
