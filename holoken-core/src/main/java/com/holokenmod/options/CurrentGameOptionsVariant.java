package com.holokenmod.options;

public class CurrentGameOptionsVariant {
	private static final GameOptionsVariant INSTANCE = new GameOptionsVariant();
	
	public static GameOptionsVariant getInstance() {
		return INSTANCE;
	}
}
