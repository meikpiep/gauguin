package com.holokenmod.creation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.holokenmod.backtrack.hybrid.MathDokuCage2BackTrack;
import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridCage;
import com.holokenmod.grid.GridCell;
import com.holokenmod.grid.GridSize;
import com.holokenmod.options.CurrentGameOptionsVariant;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GridCageOperation;
import com.holokenmod.options.SingleCageUsage;

import org.junit.jupiter.api.RepeatedTest;

public class TestGridCalculator {
	@RepeatedTest(1)
	void test3x3Grid() {
		CurrentGameOptionsVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
		CurrentGameOptionsVariant.getInstance().setShowOperators(true);
		CurrentGameOptionsVariant.getInstance().setSingleCageUsage(SingleCageUsage.FIXED_NUMBER);
		CurrentGameOptionsVariant.getInstance().setCageOperation(GridCageOperation.OPERATIONS_ALL);
		
		GridCalculator creator = new GridCalculator(
				new GridSize(9, 9));
		
		Grid grid = creator.calculate();
		
		MathDokuCage2BackTrack backTrack = new MathDokuCage2BackTrack(grid, false);
		
		int solutions = backTrack.solve();
		
		assertThat(
				"Found " + solutions + " solutions, but there should be exactly one. " + grid,
				solutions,
				is(1));
	}
	
	@RepeatedTest(1)
	void bruteForce() {
		CurrentGameOptionsVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
		CurrentGameOptionsVariant.getInstance().setShowOperators(true);
		CurrentGameOptionsVariant.getInstance().setSingleCageUsage(SingleCageUsage.FIXED_NUMBER);
		CurrentGameOptionsVariant.getInstance().setCageOperation(GridCageOperation.OPERATIONS_ALL);
		
		GridCalculator creator = new GridCalculator(
				new GridSize(4, 4));
		
		
		Grid grid = creator.calculate();
		
		solveBruteForce(grid, 0);
	}
	
	private void solveBruteForce(Grid grid, int cellNumber) {
		if (cellNumber == grid.getGridSize().getSurfaceArea()) {
			
			if (isValidSolution(grid)) {
				System.out.println("Found valid solution.");
				
				for(GridCell cell: grid.getCells()) {
					assertThat(
							"Found differing solution. " + grid,
							cell.getUserValue(), is(cell.getValue()));
				}
			}
			
			return;
		}
		
		GridCell cell = grid.getCell(cellNumber);

		for(int value : grid.getPossibleDigits()) {
			if (!grid.isUserValueUsedInSameColumn(cellNumber, value)
					&& !grid.isUserValueUsedInSameRow(cellNumber, value)) {
				
				cell.setUserValueIntern(value);
				
				solveBruteForce(grid, cellNumber + 1);
			}
		}
		
		cell.setUserValueIntern(GridCell.NO_VALUE_SET);
	}
	
	private boolean isValidSolution(Grid grid) {
		boolean validSolution = true;
		
		for( GridCell cell: grid.getCells()) {
			validSolution &= !grid.isUserValueUsedInSameColumn(cell.getCellNumber(), cell.getUserValue());
			validSolution &= !grid.isUserValueUsedInSameRow(cell.getCellNumber(), cell.getUserValue());
		}
		
		for(GridCage cage : grid.getCages()) {
			validSolution &= cage.isMathsCorrect();
		}
		
		return validSolution;
	}
}
