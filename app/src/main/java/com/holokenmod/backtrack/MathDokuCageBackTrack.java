package com.holokenmod.backtrack;

import android.util.Log;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCell;
import com.holokenmod.creation.GridCageCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MathDokuCageBackTrack {
	private final Grid grid;
	private final int maxCageIndex;
	private final ArrayList<GridCage> cages;
	private List<GridCageCreator> cageCreators = new ArrayList<>();
	private int sumSolved;
	
	public MathDokuCageBackTrack(Grid grid) {
		this.grid = grid;
		this.cages = grid.getCages();
		this.maxCageIndex = grid.getCages().size() - 1;
		this.sumSolved = 0;
	}
	
	public int solve() {
		cageCreators = cages.parallelStream()
				.map(cage -> {
					GridCageCreator creator = new GridCageCreator(grid, cage);
					creator.getPossibleNums();
					Log.i("cage possibles", "size " + creator.getPossibleNums().size());
					return creator;
				})
				.collect(Collectors.toList());
		
		solve(0);
		
		return sumSolved;
	}
	
	public void solve(int cageIndex) {
		GridCage cage = cages.get(cageIndex);
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
					Log.d("backtrack", "Found solution " + grid.toString());
					
					sumSolved++;
					
					if (sumSolved >= 2) {
						return;
					}
					
					if (!grid.isSolved()) {
						sumSolved++;
						
						return;
					}
				}
				
				for(GridCell cell : cage.getCells()) {
					cell.setUserValueIntern(-1);
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