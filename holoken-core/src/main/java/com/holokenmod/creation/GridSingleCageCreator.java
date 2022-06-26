package com.holokenmod.creation;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCageAction;
import com.holokenmod.GridCell;
import com.holokenmod.options.CurrentGameOptionsVariant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GridSingleCageCreator {
	private final static Logger LOGGER = LoggerFactory.getLogger(GridSingleCageCreator.class);
	
	private final Grid grid;
	private final GridCage cage;
	
	// Cached list of numbers which satisfy the cage's arithmetic
	private List<int[]> mPossibles = null;
	// The following two variables are required by the recursive methods below.
// They could be passed as parameters of the recursive methods, but this
// reduces performance.
	private int[] numbers;
	private ArrayList<int[]> possibleCombinations;
	
	public GridSingleCageCreator(final Grid grid, final GridCage cage) {
		this.grid = grid;
		this.cage = cage;
	}
	
	public List<int[]> getPossibleNums() {
		if (mPossibles == null) {
			if (CurrentGameOptionsVariant.getInstance().showOperators()) {
				mPossibles = setPossibleNums();
			} else {
				mPossibles = setPossibleNumsNoOperator();
			}
		}
		return mPossibles;
	}
	
	private ArrayList<int[]> setPossibleNumsNoOperator() {
		ArrayList<int[]> allResults = new ArrayList<>();
		
		if (cage.getAction() == GridCageAction.ACTION_NONE) {
			assert (cage.getNumberOfCells() == 1);
			final int[] number = {cage.getResult()};
			allResults.add(number);
			return allResults;
		}
		
		if (cage.getNumberOfCells() == 2) {
			for (final int i1 : this.grid.getPossibleDigits()) {
				for (int i2 = i1 + 1; i2 <= this.grid.getMaximumDigit(); i2++) {
					if (i2 - i1 == cage.getResult() || i1 - i2 == cage.getResult() || cage
							.getResult() * i1 == i2 ||
							cage.getResult() * i2 == i1 || i1 + i2 == cage
							.getResult() || i1 * i2 == cage.getResult()) {
						int[] numbers = {i1, i2};
						allResults.add(numbers);
						numbers = new int[]{i2, i1};
						allResults.add(numbers);
					}
				}
			}
			return allResults;
		}
		
		// ACTION_ADD:
		allResults = getalladdcombos(cage.getResult(), cage.getNumberOfCells());
		
		// ACTION_MULTIPLY:
		final ArrayList<int[]> multResults = getallmultcombos(cage.getResult(), cage
				.getNumberOfCells());
		
		// Combine Add & Multiply result sets
		for (final int[] possibleset : multResults) {
			boolean foundset = false;
			for (final int[] currentset : allResults) {
				if (Arrays.equals(possibleset, currentset)) {
					foundset = true;
					break;
				}
			}
			if (!foundset) {
				allResults.add(possibleset);
			}
		}
		
		return allResults;
	}

	/*
	 * Generates all combinations of numbers which satisfy the cage's arithmetic
	 * and MathDoku constraints i.e. a digit can only appear once in a column/row
	 */
	private List<int[]> setPossibleNums() {
		ArrayList<int[]> AllResults = new ArrayList<>();
		
		switch (cage.getAction()) {
			case ACTION_NONE:
				final int[] number = {cage.getResult()};
				return Collections.singletonList(number);
			case ACTION_SUBTRACT:
				for (final int digit : grid.getPossibleDigits()) {
					for (final int otherDigit : grid.getPossibleDigits()) {
						if (Math.abs(digit - otherDigit) == cage.getResult()) {
							AllResults.add(new int[]{digit, otherDigit});
						}
					}
				}
				return AllResults;
			case ACTION_DIVIDE:
				return getAllDivideResults();
			case ACTION_ADD:
				return getalladdcombos(cage.getResult(), cage.getNumberOfCells());
			case ACTION_MULTIPLY:
				return getallmultcombos(cage.getResult(), cage.getNumberOfCells());
		}
		
		throw new RuntimeException("Should never reach here.");
	}
	
	ArrayList<int[]> getAllDivideResults() {
		ArrayList<int[]> results = new ArrayList<>();
		
		for (final int digit : grid.getPossibleDigits()) {
			if (cage.getResult() == 0 || digit % cage.getResult() == 0) {
				int otherDigit;
				
				if (cage.getResult() == 0) {
					otherDigit = 0;
				} else {
					otherDigit = digit / cage.getResult();
				}
				
				if (digit != otherDigit && grid.getPossibleDigits().contains(otherDigit)) {
					results.add(new int[] {digit, otherDigit});
					results.add(new int[] {otherDigit, digit});
				}
			}
		}
		
		return results;
	}
	
	private ArrayList<int[]> getalladdcombos(final int target_sum, final int n_cells) {
		numbers = new int[n_cells];
		possibleCombinations = new ArrayList<>();

		getaddcombos(target_sum, n_cells);

		return possibleCombinations;
	}
	
	private void getaddcombos(final int target_sum, final int n_cells) {
		if (n_cells == 1) {
			if (grid.getPossibleDigits().contains(target_sum)) {
				numbers[0] = target_sum;
				if (satisfiesConstraints(numbers)) {
					possibleCombinations.add(numbers.clone());
				}
			}
			
			return;
		}
		
		for (final int n : grid.getPossibleDigits()) {
			numbers[n_cells - 1] = n;
			getaddcombos(target_sum - n, n_cells - 1);
		}
	}
	
	private ArrayList<int[]> getallmultcombos(final int target_sum, final int n_cells) {
		final MultiplicationCreator multipleCreator = new MultiplicationCreator(this, grid, target_sum, n_cells);
		return multipleCreator.create();
	}
	
	/*
	 * Check whether the set of numbers satisfies all constraints
	 * Looking for cases where a digit appears more than once in a column/row
	 * Constraints:
	 * 0 -> (getGrid().getGridSize() * getGrid().getGridSize())-1 = column constraints
	 * (each column must contain each digit)
	 * getGrid().getGridSize() * getGrid().getGridSize() -> 2*(getGrid().getGridSize() * getGrid().getGridSize())-1 = row constraints
	 * (each row must contain each digit)
	 */
	boolean satisfiesConstraints(final int[] test_nums) {
		int squareOfNumbers = (int) Math.round(Math.pow(grid.getGridSize().getAmountOfNumbers(), 2));
		
		final boolean[] constraints = new boolean[squareOfNumbers * 2 * 10];
		int constraint_num;
		
		for (int i = 0; i < cage.getNumberOfCells(); i++) {
			int numberToTestIndex = CurrentGameOptionsVariant.getInstance().getDigitSetting().indexOf(test_nums[i]);
			
			if (numberToTestIndex == -1) {
				LOGGER.error("No index of number " + test_nums[i] + " of cage " + cage.toString());
				System.exit(0);
			}
			
			if (test_nums[i] > grid.getMaximumDigit()) {
				LOGGER.error("Number is too big " + test_nums[i] + " of cage " + cage.toString());
				System.exit(0);
				
			}
			
			constraint_num = grid.getGridSize().getWidth() * numberToTestIndex + cage.getCell(i).getColumn();

			if (constraints[constraint_num]) {
				return false;
			}
			
			constraints[constraint_num] = true;
			
			constraint_num = squareOfNumbers
					+ grid.getGridSize().getWidth() * numberToTestIndex + cage.getCell(i).getRow();

			if (constraints[constraint_num]) {
				return false;
			}
			
			constraints[constraint_num] = true;
		}
		
		return true;
	}
	
	public int getNumberOfCells() {
		return cage.getNumberOfCells();
	}
	
	public GridCell getCell(final int i) {
		return cage.getCell(i);
	}
	
	public int getId() {
		return cage.getId();
	}
	
	public GridCage getCage() {
		return cage;
	}
}
