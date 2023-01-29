package com.holokenmod.options;

public enum GameDifficulty {
	VERY_EASY(0.0),
	EASY(5.0),
	MEDIUM(33.33),
	HARD(66.67),
	EXTREME(95.0);
	
	private final double minimumPercentage;
	
	GameDifficulty(double minimumPercentage) {
		this.minimumPercentage = minimumPercentage;
	}
}
