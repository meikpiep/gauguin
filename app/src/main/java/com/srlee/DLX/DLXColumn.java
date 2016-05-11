package com.srlee.DLX;


public class DLXColumn extends LL2DNode
{
    private int size;		// Number of items in column
    public DLXColumn()
    {
        size = 0;
        SetUp(this);
        SetDown(this);
    }
    public int GetSize() { return size; }
    public void DecSize() { size--; }
    public void IncSize() { size++; }
}
