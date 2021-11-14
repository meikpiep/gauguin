package com.holokenmod;

public enum Theme {
	LIGHT(0xFFf3efe7, 0xF0000000),
	DARK(0xFF272727, 0xFFFFFFFF);
	
	private final int backgroundColor;
	private final int textColor;
	
	Theme(final int backgroundColor, final int textColor) {
		this.backgroundColor = backgroundColor;
		this.textColor = textColor;
	}
	
	public int getBackgroundColor() {
		return backgroundColor;
	}
	
	public int getTextColor() {
		return textColor;
	}
}
