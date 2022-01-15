package com.holokenmod;

public enum Theme {
	LIGHT(0xFFf3efe7, //off-white
			0xF0000000,
			0x90e0bf9f), //light brown
	DARK(0xFF272727,
			0xFFFFFFFF,
			0x90555555), //light gray
	SYSTEM_DEFAULT(0xFF272727,
			0xFFFFFFFF,
			0x90555555);
	
	private final int backgroundColor;
	private final int textColor;
	private final int cellGridColor;
	
	Theme(final int backgroundColor, final int textColor, final int cellGridColor) {
		this.backgroundColor = backgroundColor;
		this.textColor = textColor;
		this.cellGridColor = cellGridColor;
	}
	
	public int getBackgroundColor() {
		return backgroundColor;
	}
	
	public int getTextColor() {
		return textColor;
	}
	
	public int getCellGridColor() {
		return cellGridColor;
	}
}
