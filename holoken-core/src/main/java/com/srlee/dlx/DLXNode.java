package com.srlee.dlx;

class DLXNode extends LL2DNode {
	private final DLXColumn column;
	private final int rowIndex;
	
	DLXNode(final DLXColumn column, final int rowIndex) {
		this.rowIndex = rowIndex;
		this.column = column;
		column.up.down = this;
		up = column.up;
		down = column;
		column.up = this;
		column.incrementSize();
	}
	
	DLXColumn getColumn() {
		return column;
	}
	
	int getRow() {
		return rowIndex;
	}
}
