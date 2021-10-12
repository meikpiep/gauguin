package com.srlee.DLX;

import java.util.ArrayList;

import com.holokenmod.GridCage;


public class MathDokuDLX extends DLX {

	public MathDokuDLX(int boardSize, ArrayList<GridCage> cages) {

		final int BOARD2 = boardSize * boardSize;
        
        // Number of columns = number of constraints =
        //		BOARD * BOARD (for columns) +
        //		BOARD * BOARD (for rows)	+
        //		Num cages (each cage has to be filled once and only once)
        // Number of rows = number of "moves" =
        //		Sum of all the possible cage combinations
        // Number of nodes = sum of each move:
        //      num_cells column constraints +
        //      num_cells row constraints +
        //      1 (cage constraint)
		int total_nodes = 0;
		for (GridCage gc : cages) {
       		total_nodes += gc.getPossibleNums().size()*(2*gc.mCells.size()+1);
        }
        Init (2*BOARD2 + cages.size(), total_nodes);
        
        int constraint_num;
        int move_idx = 0;
        for (GridCage gc : cages)
        {
        	ArrayList<int[]> allmoves = gc.getPossibleNums();
        	for (int[] onemove : allmoves)
        	{
        		for (int i = 0; i<gc.mCells.size(); i++) {
        			constraint_num = boardSize *(onemove[i]-1) + gc.mCells.get(i).mColumn + 1;
        			AddNode(constraint_num, move_idx);	// Column constraint
        			constraint_num = BOARD2 + boardSize *(onemove[i]-1) + gc.mCells.get(i).mRow + 1;
        			AddNode(constraint_num, move_idx);	// Row constraint
        		}
    			constraint_num = 2 * BOARD2 + gc.mId + 1;
    			AddNode(constraint_num, move_idx);	// Cage constraint
    			move_idx++;
        	}
        }
	}

}
