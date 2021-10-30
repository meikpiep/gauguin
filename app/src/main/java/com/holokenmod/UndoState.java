
package com.holokenmod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UndoState {

    private final GridCell cell;
    private final int userValue;
    private final List<Integer> possibles;
    private final boolean batch;
    
    public UndoState (GridCell cell, int userValue, List<Integer> Possibles) {
        this.cell = cell;
        this.userValue = userValue;
        this.possibles = copyArrayList(Possibles);
        this.batch = false;
    }
    
    public UndoState (GridCell cell, int userValue, List<Integer> Possibles, boolean batch) {
        this.cell = cell;
        this.userValue = userValue;
        this.possibles = copyArrayList(Possibles);
        this.batch = batch;
    }
    
    public GridCell getCell () {
        return this.cell;
    }
    
    public int getUserValue() {
        return this.userValue;
    }
    
    public List<Integer> getPossibles() {
        return this.possibles;
    }
    
    public boolean getBatch() {
        return this.batch;
    }
    
    public List<Integer> copyArrayList(List<Integer> oldlist) {
        List<Integer> copylist = Collections.synchronizedList( new ArrayList<Integer>(oldlist));
        Collections.copy(copylist,oldlist);
        return copylist;
    }
}