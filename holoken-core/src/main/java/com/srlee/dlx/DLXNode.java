package com.srlee.dlx;

class DLXNode extends LL2DNode {
	public final DLXColumn columnHeader;
	private final int rowIndex;
	
	DLXNode(final DLXColumn columnHeader, final int rowIndex) {
		this.rowIndex = rowIndex;
		this.columnHeader = columnHeader;
		columnHeader.U.D = this;
		U = columnHeader.U;
		D = columnHeader;
		columnHeader.U = this;
		columnHeader.IncSize();
	}
	
	DLXColumn GetColumn() {
		return columnHeader;
	}
	
	int GetRowIdx() {
		return rowIndex;
	}
}
