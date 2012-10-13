package com.tortuca.holoken;

import java.util.ArrayList;
import java.util.Collections;

public class UndoState {

    public int cellNum;
    public int userValue;
    public ArrayList<Integer> possibles;
    
    public UndoState (int cellNum, int userValue, ArrayList<Integer> Possibles) {
        this.cellNum = cellNum;
        this.userValue = userValue;
        this.possibles = copyArrayList(Possibles);
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
    
    public ArrayList<Integer> copyArrayList(ArrayList<Integer> oldlist) {
        ArrayList<Integer> copylist = new ArrayList<Integer>(oldlist);
        Collections.copy(copylist,oldlist);
        return copylist;
    }
}