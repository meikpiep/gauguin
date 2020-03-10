package com.holokenmod;

import java.util.LinkedList;

@SuppressWarnings("serial")
public class UndoList extends LinkedList<UndoState> {
    private int maxSize;
    
    public UndoList(int maxSize) {
        this.maxSize = maxSize;
    }
    
    @Override
    public synchronized boolean add(UndoState object) {
        //if (size() == maxSize)
        //    removeFirst();
        return super.add(object);
    }

    @Override
    public synchronized UndoState removeFirst() {
        return super.removeFirst();
    }

    @Override
    public synchronized UndoState removeLast() {
        return super.removeLast();
    }

}