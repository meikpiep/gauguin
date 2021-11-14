package com.srlee.DLX;

class LL2DNode {
	private LL2DNode L;   // Pointer to left node
	private LL2DNode R;   // Pointer to right node
	private LL2DNode U;   // Pointer to node above
	private LL2DNode D;   // Pointer to node below
	
	LL2DNode() {
		L = R = U = D = null;
	}
	
	void SetLeft(final LL2DNode left) {
		L = left;
	}
	
	void SetRight(final LL2DNode right) {
		R = right;
	}
	
	void SetUp(final LL2DNode up) {
		U = up;
	}
	
	void SetDown(final LL2DNode down) {
		D = down;
	}
	
	LL2DNode GetLeft() {
		return L;
	}
	
	LL2DNode GetRight() {
		return R;
	}
	
	LL2DNode GetUp() {
		return U;
	}
	
	LL2DNode GetDown() {
		return D;
	}
}
