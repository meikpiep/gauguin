package com.holokenmod.grid;

public enum GridCageAction {
	
	ACTION_NONE(""),
	ACTION_ADD("+"),
	ACTION_SUBTRACT("-"),
	ACTION_MULTIPLY("x"),
	ACTION_DIVIDE("/");
	
	private final String displayName;
	
	GridCageAction(final String displayName) {
		this.displayName = displayName;
	}
	
	public String getOperationDisplayName() {
		return displayName;
	}
}