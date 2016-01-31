package com.tortuca.holoken;

import java.util.ArrayList;
import java.util.Collections;

public class UndoState {

    public int cellNum;
    public int userValue;
    public ArrayList<Integer> possibles;
    public boolean batch;
    
    public UndoState (int cellNum, int userValue, ArrayList<Integer> Possibles) {
        this.cellNum = cellNum;
        this.userValue = userValue;
        this.possibles = copyArrayList(Possibles);
        this.batch = false;
    }
    
    public UndoState (int cellNum, int userValue, ArrayList<Integer> Possibles, boolean batch) {
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
    
    public ArrayList<Integer> getPossibles() {
        return this.possibles;
    }
    
    public boolean getBatch() {
        return this.batch;
    }
    
    public ArrayList<Integer> copyArrayList(ArrayList<Integer> oldlist) {
        ArrayList<Integer> copylist = new ArrayList<Integer>(oldlist);
        Collections.copy(copylist,oldlist);
        return copylist;
    }
}