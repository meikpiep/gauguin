package com.holokenmod.backtrack;

import android.util.Log;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCell;
import com.holokenmod.creation.GridSingleCageCreator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MathDokuCageBackTrack {
	private final Grid grid;
	private final int maxCageIndex;
	private final ArrayList<GridCage> cages = new ArrayList<>();
	private final boolean isPreSolved;
	private List<GridSingleCageCreator> cageCreators = new ArrayList<>();
	private int sumSolved;
	
	public MathDokuCageBackTrack(Grid grid, boolean isPreSolved) {
		this.grid = grid;
		this.isPreSolved = isPreSolved;
		this.maxCageIndex = grid.getCages().size() - 1;
		this.sumSolved = 0;
	}
	
	public int solve() {
		cageCreators = grid.getCages().parallelStream()
				.map(cage -> new GridSingleCageCreator(grid, cage))
				.sorted(Comparator.comparingInt(o -> o.getPossibleNums().size()))
				.collect(Collectors.toList());
		
		cages.clear();
		
		for(GridSingleCageCreator creator : cageCreators) {
			cages.add(creator.getCage());
		}
		
		solve(0);
		
		return sumSolved;
	}
	
	public void solve(int cageIndex) {
		GridCage cage = cages.get(cageIndex);
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
					Log.d("backtrack", "Found solution " + grid.toString());
					
					sumSolved++;
					
					if (sumSolved >= 2) {
						return;
					}
					
					if (isPreSolved && !grid.isSolved()) {
						sumSolved++;
						
						return;
					}
				}
				
				for(GridCell cell : cage.getCells()) {
					cell.setUserValueIntern(GridCell.NO_VALUE_SET);
				}
			}
			//currentCell.setUserValueIntern(-1);
			
			if (sumSolved >= 2) {
				return;
			}
		}
	}
	
	private boolean areCellsValid(GridCage cage, int[] possibleCombination) {
		int i = 0;
		
		for(GridCell cell : cage.getCells()) {
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