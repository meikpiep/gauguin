package com.srlee.dlx;

class DLXColumn extends LL2DNode {
	private int size;        // Number of items in column
	
	DLXColumn() {
		size = 0;
		U = this;
		D = this;
	}
	
	int GetSize() {
		return size;
	}
	
	void DecSize() {
		size--;
	}
	
	void IncSize() {
		size++;
	}
}
