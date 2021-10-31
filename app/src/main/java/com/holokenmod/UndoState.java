
package com.holokenmod;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class UndoState {

    private final GridCell cell;
    private final int userValue;
    private final SortedSet<Integer> possibles;
    private final boolean batch;
    
    public UndoState (GridCell cell, int userValue, SortedSet<Integer> Possibles) {
        this.cell = cell;
        this.userValue = userValue;
        this.possibles = copySet(Possibles);
        this.batch = false;
    }
    
    public UndoState (GridCell cell, int userValue, SortedSet<Integer> Possibles, boolean batch) {
        this.cell = cell;
        this.userValue = userValue;
        this.possibles = copySet(Possibles);
        this.batch = batch;
    }
    
    public GridCell getCell () {
        return this.cell;
    }
    
    public int getUserValue() {
        return this.userValue;
    }
    
    public SortedSet<Integer> getPossibles() {
        return this.possibles;
    }
    
    public boolean getBatch() {
        return this.batch;
    }
    
    public SortedSet<Integer> copySet(SortedSet<Integer> oldSet) {
        return Collections.synchronizedSortedSet(new TreeSet<>(oldSet));
    }
}