package com.srlee.DLX;

public class LL2DNode extends Object
{
    public void SetLeft(LL2DNode left) { L = left; }
    public void SetRight(LL2DNode right) { R = right; }
    public void SetUp(LL2DNode up) { U = up; }
    public void SetDown(LL2DNode down) { D = down; }
    public LL2DNode GetLeft() { return L; }
    public LL2DNode GetRight() { return R; }
    public LL2DNode GetUp() { return U; }
    public LL2DNode GetDown() { return D; }
    public LL2DNode()
    {
        L = R = U = D = null;
    }

    private LL2DNode L;   // Pointer to left node
    private LL2DNode R;   // Pointer to right node
    private LL2DNode U;   // Pointer to node above
    private LL2DNode D;   // Pointer to node below
}
