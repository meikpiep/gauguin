package com.holokenmod.options;

public enum GridCageDefaultOperation {
	ASK(0, null),
	OPERATIONS_ALL(1, GridCageOperation.OPERATIONS_ALL),
	OPERATIONS_ADD_SUB(2, GridCageOperation.OPERATIONS_ADD_SUB),
	OPERATIONS_ADD_MULT(3, GridCageOperation.OPERATIONS_ADD_MULT),
	OPERATIONS_MULT(4, GridCageOperation.OPERATIONS_MULT);
	
	private final int id;
	private final GridCageOperation operation;
	
	GridCageDefaultOperation(int id, GridCageOperation operation) {
		this.id = id;
		this.operation = operation;
	}
	
	public static GridCageDefaultOperation getById(int id) {
		for(GridCageDefaultOperation item : values()) {
			if (item.getId() == id) {
				return item;
			}
		}
		
		return null;
	}
	
	public int getId() {
		return this.id;
	}
	
	public GridCageOperation getOperation() {
		return operation;
	}
}
