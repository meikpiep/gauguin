package com.srlee.DLX;


class DLXColumn extends LL2DNode
{
    private int size;		// Number of items in column

    DLXColumn() {
        size = 0;
        SetUp(this);
        SetDown(this);
    }

    int GetSize() { return size; }
    void DecSize() { size--; }
    void IncSize() { size++; }
}
