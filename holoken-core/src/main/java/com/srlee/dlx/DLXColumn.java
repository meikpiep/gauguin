package com.srlee.dlx;

class DLXColumn extends LL2DNode {
	private int size;        // Number of items in column
	
	DLXColumn() {
		size = 0;
		up = this;
		down = this;
	}
	
	int getSize() {
		return size;
	}
	
	void decrementSize() {
		size--;
	}
	
	void incrementSize() {
		size++;
	}
}
