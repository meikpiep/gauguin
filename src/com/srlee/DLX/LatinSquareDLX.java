package com.srlee.DLX;

import java.util.ArrayList;

import com.tortuca.holoken.GridCell;


public class LatinSquareDLX extends DLX {

    private int BOARD = 0;
    private int BOARD2 = 0;
    private int BOARD3 = 0;
    
    public LatinSquareDLX (int n, ArrayList<GridCell> cells)
    {
        BOARD = n;
        BOARD2 = BOARD * BOARD;
        BOARD3 = BOARD2 * BOARD;
        
        Init(BOARD2 * 3, BOARD3, BOARD3 * 3);
        
        int d, r, c;
        int moveidx = 0;

        // Setup all possible "moves" and the conditions they affect
         for (d = 1; d <= BOARD; d++)
            for (r = 1; r <= BOARD; r++)
                for (c = 1; c <= BOARD; c++)
                {
                    AddNode((r - 1) * BOARD + c, moveidx);			    // <r,c>
                    AddNode(BOARD2 + (d - 1) * BOARD + r, moveidx);	    // <d,r>
                    AddNode(BOARD2 * 2 + (d - 1) * BOARD + c, moveidx);	// <d,c>
                    moveidx++;
                }
        
        // Now apply the "moves" we already know
        for (GridCell cell : cells)
        	if (cell.mValue != 0)
        		if (!GivenRow((cell.mValue - 1)* BOARD2 + cell.mRow * BOARD + cell.mColumn + 1)) {
        			isValid = false;
        			return;
        		}
    }
}
