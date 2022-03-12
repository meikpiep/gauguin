package com.srlee.dlx;

class LL2DNode {
	LL2DNode L;   // Pointer to left node
	LL2DNode R;   // Pointer to right node
	LL2DNode U;   // Pointer to node above
	LL2DNode D;   // Pointer to node below
	
	LL2DNode() {
		L = R = U = D = null;
	}
}
