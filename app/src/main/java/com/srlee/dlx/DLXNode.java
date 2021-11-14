package com.srlee.dlx;

class DLXNode extends LL2DNode {
	private final DLXColumn columnHeader;
	private final int rowIndex;
	
	DLXNode(final DLXColumn columnHeader, final int rowIndex) {
		this.rowIndex = rowIndex;
		this.columnHeader = columnHeader;
		columnHeader.GetUp().SetDown(this);
		SetUp(columnHeader.GetUp());
		SetDown(columnHeader);
		columnHeader.SetUp(this);
		columnHeader.IncSize();
	}
	
	DLXColumn GetColumn() {
		return columnHeader;
	}
	
	int GetRowIdx() {
		return rowIndex;
	}
}
