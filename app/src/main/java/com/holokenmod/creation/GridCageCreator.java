package com.holokenmod.creation;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCageAction;
import com.holokenmod.GridCell;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GameVariant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GridCageCreator {
	private final Grid grid;
	private final GridCage cage;
	
	// Cached list of numbers which satisfy the cage's arithmetic
	private List<int[]> mPossibles = null;
	// The following two variables are required by the recursive methods below.
// They could be passed as parameters of the recursive methods, but this
// reduces performance.
	private int[] numbers;
	private ArrayList<int[]> result_set;
	
	public GridCageCreator(final Grid grid, final GridCage cage) {
		this.grid = grid;
		this.cage = cage;
	}
	
	public List<int[]> getPossibleNums() {
		if (mPossibles == null) {
			if (GameVariant.getInstance().showOperators()) {
				mPossibles = setPossibleNums();
			} else {
				mPossibles = setPossibleNumsNoOperator();
			}
		}
		return mPossibles;
	}
	
	private ArrayList<int[]> setPossibleNumsNoOperator() {
		ArrayList<int[]> AllResults = new ArrayList<>();
		
		if (cage.getAction() == GridCageAction.ACTION_NONE) {
			assert (cage.getNumberOfCells() == 1);
			final int[] number = {cage.getResult()};
			AllResults.add(number);
			return AllResults;
		}
		
		if (cage.getNumberOfCells() == 2) {
			for (final int i1 : this.grid.getPossibleDigits()) {
				for (int i2 = i1 + 1; i2 <= this.grid.getMaximumDigit(); i2++) {
					if (i2 - i1 == cage.getResult() || i1 - i2 == cage.getResult() || cage
							.getResult() * i1 == i2 ||
							cage.getResult() * i2 == i1 || i1 + i2 == cage
							.getResult() || i1 * i2 == cage.getResult()) {
						int[] numbers = {i1, i2};
						AllResults.add(numbers);
						numbers = new int[]{i2, i1};
						AllResults.add(numbers);
					}
				}
			}
			return AllResults;
		}
		
		// ACTION_ADD:
		AllResults = getalladdcombos(cage.getResult(), cage.getNumberOfCells());
		
		// ACTION_MULTIPLY:
		final ArrayList<int[]> multResults = getallmultcombos(cage.getResult(), cage
				.getNumberOfCells());
		
		// Combine Add & Multiply result sets
		for (final int[] possibleset : multResults) {
			boolean foundset = false;
			for (final int[] currentset : AllResults) {
				if (Arrays.equals(possibleset, currentset)) {
					foundset = true;
					break;
				}
			}
			if (!foundset) {
				AllResults.add(possibleset);
			}
		}
		
		return AllResults;
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
				for (final int i1 : grid.getPossibleDigits()) {
					for (int i2 = i1 + 1; i2 <= grid.getMaximumDigit(); i2++) {
						if (i2 - i1 == cage.getResult() || i1 - i2 == cage.getResult()) {
							int[] numbers = {i1, i2};
							AllResults.add(numbers);
							numbers = new int[]{i2, i1};
							AllResults.add(numbers);
						}
					}
				}
				return AllResults;
			case ACTION_DIVIDE:
				for (final int i1 : grid.getPossibleDigits()) {
					for (int i2 = i1 + 1; i2 <= grid.getMaximumDigit(); i2++) {
						if (cage.getResult() * i1 == i2 || cage.getResult() * i2 == i1 && i1 != 0 && i2 != 0) {
							int[] numbers = {i1, i2};
							AllResults.add(numbers);
							numbers = new int[]{i2, i1};
							AllResults.add(numbers);
						}
					}
				}
				return AllResults;
			case ACTION_ADD:
				return getalladdcombos(cage.getResult(), cage.getNumberOfCells());
			case ACTION_MULTIPLY:
				return getallmultcombos(cage.getResult(), cage.getNumberOfCells());
		}
		
		throw new RuntimeException("Should never reach here.");
	}
	
	private ArrayList<int[]> getalladdcombos(final int target_sum, final int n_cells) {
		numbers = new int[n_cells];
		result_set = new ArrayList<>();

		getaddcombos(target_sum, n_cells);

		return result_set;
	}
	
	private void getaddcombos(final int target_sum, final int n_cells) {
		if (n_cells == 1) {
			if (grid.getPossibleDigits().contains(target_sum)) {
				numbers[0] = target_sum;
				if (satisfiesConstraints(numbers)) {
					result_set.add(numbers.clone());
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
		final boolean[] constraints = new boolean[grid.getGridSize() * grid.getGridSize() * 2];
		int constraint_num;
		
		for (int i = 0; i < cage.getNumberOfCells(); i++) {
			int numberToTestIndex = test_nums[i];
			
			if (GameVariant.getInstance()
					.getDigitSetting() == DigitSetting.FIRST_DIGIT_ONE) {
				numberToTestIndex = numberToTestIndex - 1;
			}
			
			constraint_num = grid.getGridSize() * numberToTestIndex + cage.getCell(i).getColumn();
			if (constraints[constraint_num]) {
				return false;
			} else {
				constraints[constraint_num] = true;
			}
			
			constraint_num = grid.getGridSize() * grid.getGridSize()
					+ grid.getGridSize() * numberToTestIndex + cage.getCell(i).getRow();
			if (constraints[constraint_num]) {
				return false;
			} else {
				constraints[constraint_num] = true;
			}
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
