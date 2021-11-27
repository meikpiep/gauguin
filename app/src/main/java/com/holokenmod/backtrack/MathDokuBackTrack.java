package com.holokenmod.backtrack;

import android.util.Log;

import com.holokenmod.Grid;
import com.holokenmod.GridCell;

import java.util.Collection;

public class MathDokuBackTrack {
	private final Grid grid;
	private final int maxCellIndex;
	private final Collection<Integer> possibleDigits;
	private int sumSolved;
	
	public MathDokuBackTrack(Grid grid) {
		this.grid = grid;
		this.maxCellIndex = grid.getGridSize() * grid.getGridSize() - 1;
		this.possibleDigits = grid.getPossibleDigits();
		this.sumSolved = 0;
	}
	
	public int solve() {
		solve(0);
		
		return sumSolved;
	}
	
	public void solve(int cellIndex) {
		for(int number : possibleDigits) {
			//Log.d("backtrack", "Stepping " + System.lineSeparator() + grid.toStringCellsOnly());
			final GridCell currentCell = grid.getCell(cellIndex);
			
			if (!grid.isValueUsedInSameRow(cellIndex, number)
					&& !grid.isValueUsedInSameColumn(cellIndex, number)) {
				
				currentCell.setUserValueIntern(number);
				
				if (isCageCorrectOrUnfilled(currentCell)) {
					if (cellIndex < maxCellIndex) {
						solve(cellIndex + 1);
					} else {
						Log.d("backtrack", "Found solution " + grid.toString());
						
						sumSolved++;
						
						if (!grid.isSolved()) {
							sumSolved++;
						}
					}
				}
				
				currentCell.setUserValueIntern(-1);
				
				if (sumSolved >= 2) {
					return;
				}
			}
		}
		
		return;
	}
	
	private boolean isCageCorrectOrUnfilled(GridCell cell) {
		if (cell.getCage().getLastCell() != cell) {
			return cell.getCage().isPartialFilledMathsCorrect();
		}
		
		return cell.getCage().isMathsCorrect();
	}
}
