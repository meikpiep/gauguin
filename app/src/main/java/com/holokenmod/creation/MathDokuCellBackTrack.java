package com.holokenmod.creation;

import android.util.Log;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCell;

public class MathDokuCellBackTrack {
	private final Grid grid;
	private final int maxCageIndex;
	private int sumSolved;
	
	public MathDokuCellBackTrack(Grid grid) {
		this.grid = grid;
		this.maxCageIndex = grid.getCages().size() - 1;
		this.sumSolved = 0;
	}
	
	public int solve() {
		solve(0);
		
		return sumSolved;
	}
	
	public void solve(int cageIndex) {
		GridCage cage = grid.getCages().get(cageIndex);
		GridCageCreator cageCreator = new GridCageCreator(grid, cage);
		
		for(int[] possibleCombination : cageCreator.getPossibleNums()) {
			//Log.d("backtrack", "Stepping " + System.lineSeparator() + grid.toStringCellsOnly());
			
			int i = 0;
			boolean validCells = true;
			
			for(GridCell cell : cage.getCells()) {
				if (grid.isValueUsedLeftOf(cell.getCellNumber(), possibleCombination[i])
						|| grid.isValueUsedAboveOf(cell.getCellNumber(), possibleCombination[i])) {
					validCells = false;
				}
				
				i++;
			}
			
			if (validCells && cageCreator.satisfiesConstraints(possibleCombination)) {
				
				int cellNumber = 0;
				
				for(GridCell cell : cage.getCells()) {
					cell.setUserValueIntern(possibleCombination[cellNumber]);
					
					cellNumber++;
				}
				
				if (cageIndex < maxCageIndex) {
					solve(cageIndex + 1);
				} else {
					Log.d("backtrack", "Found solution " + grid.toString());
					
					sumSolved++;
				}
				
				//currentCell.setUserValueIntern(-1);
				
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
