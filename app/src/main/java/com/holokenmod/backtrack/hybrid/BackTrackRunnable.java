package com.holokenmod.backtrack.hybrid;

import android.util.Log;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCell;
import com.holokenmod.creation.GridCageCreator;

import java.util.Arrays;
import java.util.List;

public class BackTrackRunnable implements Runnable {
	private final int[] combination;
	private Grid grid;
	private List<GridCageCreator> cageCreators;
	private boolean isPreSolved;
	private BackTrackSolutionListener solutionListener;
	private int maxCageIndex;
	
	public BackTrackRunnable(int[] combination) {
		this.combination = combination;
	}
	
	@Override
	public void run() {
		grid = ((BackTrackThread) Thread.currentThread()).grid;
		maxCageIndex = grid.getCages().size() - 1;
		cageCreators = ((BackTrackThread) Thread.currentThread()).cageCreators;
		isPreSolved = ((BackTrackThread) Thread.currentThread()).isPreSolved;
		solutionListener = ((BackTrackThread) Thread.currentThread()).solutionListener;
		
		grid.clearUserValues();
		
		setCombination();
		
		Log.d("back2", "Solving " + Arrays.toString(combination));
		
		//Log.d("back2", grid.toString());
		
		try {
			solve(combination.length);
		} catch (InterruptedException e) {
		}
	}
	
	private void setCombination() {
		for (int cageIndex = 0; cageIndex < combination.length; cageIndex++) {
			GridCage cage = grid.getCages().get(cageIndex);
			
			int[] cageCombination = cageCreators.get(cageIndex)
					.getPossibleNums()
					.get(combination[cageIndex]);
			
			int cellNumber = 0;
			
			for(GridCell cell : cage.getCells()) {
				cell.setUserValueIntern(cageCombination[cellNumber]);
				
				cellNumber++;
			}
		}
	}
	
	public void solve(int cageIndex) throws InterruptedException {
		if (Thread.currentThread().isInterrupted()) {
			throw new InterruptedException();
		}
		
		GridCage cage = grid.getCages().get(cageIndex);
		GridCageCreator cageCreator = cageCreators.get(cageIndex);
		
		for(int[] possibleCombination : cageCreator.getPossibleNums()) {
			boolean validCells = areCellsValid(cage, possibleCombination);
			
			if (validCells) {
				int cellNumber = 0;
				
				for(GridCell cell : cage.getCells()) {
					cell.setUserValueIntern(possibleCombination[cellNumber]);
					
					cellNumber++;
				}
				
				//Log.d("backtrack", "Stepping,  " + validCells
				//		+ " constraints " + cageCreator.satisfiesConstraints(possibleCombination)
				//		+ System.lineSeparator() + grid.toStringCellsOnly());
				
				if (cageIndex < maxCageIndex) {
					solve(cageIndex + 1);
				} else {
					Log.d("back2", "Found solution with " + Arrays.toString(combination) + grid.toString());
					
					solutionListener.solutionFound();
					
					if (isPreSolved && !grid.isSolved()) {
						solutionListener.solutionFound();
						
						return;
					}
				}
				
				for(GridCell cell : cage.getCells()) {
					cell.setUserValueIntern(-1);
				}
			}
		}
	}
	
	private boolean areCellsValid(GridCage cage, int[] possibleCombination) {
		int i = 0;
		
		for (GridCell cell : cage.getCells()) {
			if (grid.isValueUsedInSameRow(cell.getCellNumber(), possibleCombination[i])
					|| grid.isValueUsedInSameColumn(cell.getCellNumber(), possibleCombination[i])) {
				//		Log.d("backtrack", "Invalid cell " + cell.getCellNumber()
				//				+  ", value " + possibleCombination[i]);
				return false;
			}
			
			i++;
		}
		
		return true;
	}
}
