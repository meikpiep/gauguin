package com.holokenmod.backtrack.hybrid;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCell;
import com.holokenmod.creation.GridSingleCageCreator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class BackTrackRunnable implements Runnable {
	private final Logger LOGGER = LoggerFactory.getLogger(BackTrackRunnable.class);
	
	private final int[] combination;
	private Grid grid;
	private List<GridSingleCageCreator> cageCreators;
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
		GridSingleCageCreator cageCreator = cageCreators.get(cageIndex);
		
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
					LOGGER.debug("Found solution with " + Arrays.toString(combination) + grid.toString());
					
					solutionListener.solutionFound();
					
					if (isPreSolved && !grid.isSolved()) {
						solutionListener.solutionFound();
						
						return;
					}
				}
				
				for(GridCell cell : cage.getCells()) {
					cell.setUserValueIntern(GridCell.NO_VALUE_SET);
				}
			}
		}
	}
	
	private boolean areCellsValid(GridCage cage, int[] possibleCombination) {
		int i = 0;
		
		for (GridCell cell : cage.getCells()) {
			if (grid.isUserValueUsedInSameRow(cell.getCellNumber(), possibleCombination[i])
					|| grid.isUserValueUsedInSameColumn(cell.getCellNumber(), possibleCombination[i])) {
				//		Log.d("backtrack", "Invalid cell " + cell.getCellNumber()
				//				+  ", value " + possibleCombination[i]);
				return false;
			}
			
			i++;
		}
		
		return true;
	}
}
