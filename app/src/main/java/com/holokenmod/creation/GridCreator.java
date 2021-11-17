package com.holokenmod.creation;

import android.util.Log;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCell;
import com.holokenmod.RandomSingleton;
import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GameVariant;
import com.holokenmod.options.GridCageOperation;
import com.holokenmod.options.SingleCageUsage;
import com.srlee.dlx.DLX;
import com.srlee.dlx.MathDokuDLX;

import java.util.ArrayList;

public class GridCreator {
	
	private static final int SINGLE_CELL_CAGE = 0;
	
	// O = Origin (0,0) - must be the upper leftmost cell
	// X = Other cells used in cage
	private static final int[][][] CAGE_COORDS = new int[][][]{
			// O
			{{0, 0}},
			// O
			// X
			{{0, 0}, {0, 1}},
			// OX
			{{0, 0}, {1, 0}},
			// O
			// X
			// X
			{{0, 0}, {0, 1}, {0, 2}},
			// OXX
			{{0, 0}, {1, 0}, {2, 0}},
			// O
			// XX
			{{0, 0}, {0, 1}, {1, 1}},
			// O
			//XX
			{{0, 0}, {0, 1}, {-1, 1}},
			// OX
			//  X
			{{0, 0}, {1, 0}, {1, 1}},
			// OX
			// X
			{{0, 0}, {1, 0}, {0, 1}},
			// OX
			// XX
			{{0, 0}, {1, 0}, {0, 1}, {1, 1}},
			// OX
			// X
			// X
			{{0, 0}, {1, 0}, {0, 1}, {0, 2}},
			// OX
			//  X
			//  X
			//{{0,0},{1,0},{1,1},{1,2}},
			// O
			// X
			// XX
			//{{0,0},{0,1},{0,2},{1,2}},
			// O
			// X
			//XX
			{{0, 0}, {0, 1}, {0, 2}, {-1, 2}},
			// OXX
			// X
			{{0, 0}, {1, 0}, {2, 0}, {0, 1}},
			// OXX
			//   X
			{{0, 0}, {1, 0}, {2, 0}, {2, 1}},
			// O
			// XXX
			/*{{0,0},{0,1},{1,1},{2,1}},
			//  O
			//XXX
			{{0,0},{-2,1},{-1,1},{0,1}},
			// O
			// XX
			// X
			{{0,0},{0,1},{0,2},{1,1}},
			// O
			//XX
			// X
			{{0,0},{0,1},{0,2},{-1,1}},
			// OXX
			//  X
			{{0,0},{1,0},{2,0},{1,1}},
			// O
			//XXX
			{{0,0},{-1,1},{0,1},{1,1}},
			// OXXX
			{{0,0},{1,0},{2,0},{3,0}},
			// O
			// X
			// X
			// X
			{{0,0},{0,1},{0,2},{0,3}},
			// O
			// XX
			//  X
			{{0,0},{0,1},{1,1},{1,2}},
			// O
			//XX
			//X
			{{0,0},{0,1},{-1,1},{-1,2}},
			// OX
			//  XX
			{{0,0},{1,0},{1,1},{2,1}},
			// OX
			//XX
			{{0,0},{1,0},{0,1},{-1,1}}*/
	};
	
	private final int gridSize;
	private Grid grid;
	
	public GridCreator(final int gridSize) {
		this.gridSize = gridSize;
	}
	
	private int CreateSingleCages() {
		final int singles = grid.getGridSize() / 2;
		
		final boolean[] RowUsed = new boolean[grid.getGridSize()];
		final boolean[] ColUsed = new boolean[grid.getGridSize()];
		final boolean[] ValUsed = new boolean[grid.getGridSize()];
		
		for (int i = 0; i < singles; i++) {
			GridCell cell;
			int cellIndex;
			do {
				cell = grid.getCell(RandomSingleton.getInstance()
						.nextInt(grid.getGridSize() * grid.getGridSize()));
				
				cellIndex = cell.getValue();
				
				if (ApplicationPreferences.getInstance()
						.getDigitSetting() == DigitSetting.FIRST_DIGIT_ONE) {
					cellIndex--;
				}
				
			} while (RowUsed[cell.getRow()] || ColUsed[cell.getRow()] || ValUsed[cellIndex]);
			ColUsed[cell.getColumn()] = true;
			RowUsed[cell.getRow()] = true;
			ValUsed[cellIndex] = true;
			final GridCage cage = new GridCage(grid, SINGLE_CELL_CAGE);
			cage.addCell(cell);
			cage.setSingleCellArithmetic();
			cage.setCageId(i);
			grid.addCage(cage);
		}
		return singles;
	}
	
	/* Take a filled grid and randomly create cages */
	private void CreateCages() {
		
		final GridCageOperation operationSet = GameVariant.getInstance().getCageOperation();
		boolean restart;
		
		do {
			restart = false;
			
			int cageId = 0;
			
			if (ApplicationPreferences.getInstance()
					.getSingleCageUsage() == SingleCageUsage.FIXED_NUMBER) {
				cageId = CreateSingleCages();
			}
			
			for (final GridCell cell : grid.getCells()) {
				if (cell.CellInAnyCage()) {
					continue;
				}
				
				final ArrayList<Integer> possible_cages = getValidCages(grid, cell);
				
				final int cage_type;
				
				if (possible_cages.size() == 1) {
					// Only possible cage is a single
					if (ApplicationPreferences.getInstance()
							.getSingleCageUsage() != SingleCageUsage.DYNAMIC) {
						grid.ClearAllCages();
						restart = true;
						break;
					} else {
						cage_type = 0;
					}
				} else {
					cage_type = possible_cages.get(RandomSingleton.getInstance()
							.nextInt(possible_cages.size() - 1) + 1);
				}
				
				final GridCage cage = new GridCage(grid, cage_type);
				final int[][] cage_coords = CAGE_COORDS[cage_type];
				for (final int[] cage_coord : cage_coords) {
					final int col = cell.getColumn() + cage_coord[0];
					final int row = cell.getRow() + cage_coord[1];
					cage.addCell(grid.getCellAt(row, col));
				}
				
				cage.setArithmetic(operationSet);
				cage.setCageId(cageId++);
				grid.addCage(cage);
			}
		} while (restart);
		
		for (final GridCage cage : grid.getCages()) {
			cage.setBorders();
		}
		grid.setCageTexts();
	}
	
	private ArrayList<Integer> getValidCages(final Grid grid, final GridCell origin) {
		final ArrayList<Integer> valid = new ArrayList<>();
		
		for (int cage_num = 0; cage_num < CAGE_COORDS.length; cage_num++) {
			final int[][] cage_coords = CAGE_COORDS[cage_num];
			
			boolean validCage = true;
			
			for (final int[] cage_coord : cage_coords) {
				final int col = origin.getColumn() + cage_coord[0];
				final int row = origin.getRow() + cage_coord[1];
				final GridCell c = grid.getCellAt(row, col);
				if (c == null || c.CellInAnyCage()) {
					validCage = false;
					break;
				}
			}
			
			if (validCage) {
				valid.add(cage_num);
			}
		}
		
		return valid;
	}
	
	/*
	 * Fills the grid with random numbers, per the rules:
	 *
	 * - 1 to <rowsize> on every row and column
	 * - No duplicates in any row or column.
	 */
	private void randomiseGrid() {
		int attempts;

		final int min = ApplicationPreferences.getInstance().getDigitSetting().getMinimumDigit();
		final int max = ApplicationPreferences.getInstance().getDigitSetting()
				.getMaximumDigit(gridSize);
		
		for (int digit = min; digit <= max; digit++) {
			for (int row = 0; row < grid.getGridSize(); row++) {
				attempts = 20;
				GridCell cell;
				int column;
				while (true) {
					column = RandomSingleton.getInstance().nextInt(grid.getGridSize());
					cell = grid.getCellAt(row, column);
					
					if (--attempts == 0) {
						break;
					}
					if (cell.getValue() > -1) {
						continue;
					}
					if (grid.valueInColumn(column, digit)) {
						continue;
					}
					break;
				}
				if (attempts == 0) {
					grid.clearValue(digit--);
					break;
				}
				cell.setValue(digit);
			}
		}

//        for(GridCell cell : grid.getCells()) {
//            Log.d("KenKen", "New cell: " + cell);
//        }
	}
	
	public Grid create() {
		int num_solns;
		int num_attempts = 0;
		RandomSingleton.getInstance().discard();
		
		long sumMillis = 0;
		
		do {
			grid = new Grid(gridSize);
			
			int cellnum = 0;
			
			for (int row = 0; row < grid.getGridSize(); row++) {
				for (int column = 0; column < grid.getGridSize(); column++) {
					grid.addCell(new GridCell(cellnum++, row, column));
				}
			}
			
			randomiseGrid();
			CreateCages();
			
			num_attempts++;
			
			long milliseconds = System.currentTimeMillis();
			
			final MathDokuDLX mdd = new MathDokuDLX(grid);
			// Stop solving as soon as we find multiple solutions
			num_solns = mdd.Solve(DLX.SolveType.MULTIPLE);
			
			long durationMillis = System.currentTimeMillis() - milliseconds;
			sumMillis += durationMillis;
			
			Log.d("MathDoku", "Num Solns = " + num_solns + " in " + durationMillis + " ms");
		} while (num_solns != 1);
		
		long average = sumMillis / num_attempts;
		
		Log.d("MathDoku", "Num Attempts = " + num_attempts + " in " + sumMillis + " ms" + " (average " + average + " ms)");
		
		return grid;
	}
}