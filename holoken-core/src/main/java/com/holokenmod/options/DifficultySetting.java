package com.holokenmod.options;

public enum DifficultySetting {
	ANY(null),
	VERY_EASY(GameDifficulty.VERY_EASY),
	EASY(GameDifficulty.EASY),
	MEDIUM(GameDifficulty.MEDIUM),
	HARD(GameDifficulty.HARD),
	EXTREME(GameDifficulty.EXTREME);
	
	private final GameDifficulty gameDifficulty;
	
	DifficultySetting(GameDifficulty gameDifficulty) {
		this.gameDifficulty = gameDifficulty;
	}
	
	public GameDifficulty getGameDifficulty() {
		return gameDifficulty;
	}
}
