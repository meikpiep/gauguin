package com.holokenmod.creation;

import android.util.Log;

import com.holokenmod.Grid;
import com.holokenmod.GridCell;

public class MathDokuBackTrack {
	private final Grid grid;
	private final int maxCellIndex;
	
	public MathDokuBackTrack(Grid grid) {
		this.grid = grid;
		this.maxCellIndex = grid.getGridSize() * grid.getGridSize() - 1;
	}
	
	public int solve() {
		return solve(0);
	}
	
	public int solve(int cellIndex) {
		
		GridCell currentCell = grid.getCell(cellIndex);
		int sumSolved = 0;
		
		for(int number : grid.getPossibleDigits()) {
			currentCell.setUserValueIntern(number);
			//Log.d("backtrack", "Stepping " + grid.toStringCellsOnly());
			if (grid.getNumValueInCol(currentCell) == 1
					&& grid.getNumValueInRow(currentCell) == 1
					&& isCageCorrectOrUnfilled(currentCell)) {
				if (cellIndex < maxCellIndex) {
					sumSolved += solve(cellIndex + 1);
				} else {
					Log.d("backtrack", "Found solution " + grid.toString());
					
					sumSolved++;
				}
				
				if (sumSolved >= 2) {
					return 2;
				}
			}
			
			currentCell.setUserValueIntern(-1);
		}
		
		return sumSolved;
	}
	
	private boolean isCageCorrectOrUnfilled(GridCell cell) {
		if (cell.getCage().getLastCell() != cell) {
			return true;
		}
		
		return cell.getCage().isMathsCorrect();
	}
}
