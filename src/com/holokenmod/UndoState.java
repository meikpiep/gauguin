
package com.holokenmod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UndoState {

    public int cellNum;
    public int userValue;
    public List<Integer> possibles;
    public boolean batch;
    
    public UndoState (int cellNum, int userValue, List<Integer> Possibles) {
        this.cellNum = cellNum;
        this.userValue = userValue;
        this.possibles = copyArrayList(Possibles);
        this.batch = false;
    }
    
    public UndoState (int cellNum, int userValue, List<Integer> Possibles, boolean batch) {
        this.cellNum = cellNum;
        this.userValue = userValue;
        this.possibles = copyArrayList(Possibles);
        this.batch = batch;
    }
    
    public int getCellNum () {
        return this.cellNum;
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