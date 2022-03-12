package com.holokenmod.creation;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCageAction;
import com.holokenmod.GridCell;
import com.holokenmod.RandomSingleton;
import com.holokenmod.options.GameVariant;
import com.holokenmod.options.GridCageOperation;
import com.holokenmod.options.SingleCageUsage;

import java.util.ArrayList;
import java.util.Optional;

public class GridCageCreator {
	
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
			//=== 9 ===
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
			//=== 11 ===
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
	
	private final Grid grid;
	
	public GridCageCreator(Grid grid) {
		this.grid = grid;
	}
	
	public void createCages() {
		final GridCageOperation operationSet = GameVariant.getInstance().getCageOperation();
		boolean restart;
		
		do {
			restart = false;
			
			int cageId = 0;
			
			if (GameVariant.getInstance()
					.getSingleCageUsage() == SingleCageUsage.FIXED_NUMBER) {
				cageId = createSingleCages();
			}
			
			for (final GridCell cell : grid.getCells()) {
				if (cell.CellInAnyCage()) {
					continue;
				}
				
				final ArrayList<Integer> possible_cages = getValidCages(grid, cell);
				
				final int cage_type;
				
				if (possible_cages.size() == 1) {
					// Only possible cage is a single
					if (GameVariant.getInstance()
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
				
				final GridCage cage = GridCage.createWithCells(grid, cell, CAGE_COORDS[cage_type]);
				
				calculateCageArithmetic(cage, operationSet);
				cage.setCageId(cageId++);
				grid.addCage(cage);
			}
		} while (restart);
		
		grid.updateBorders();
		grid.setCageTexts();
	}
	
	private int createSingleCages() {
		final int singles = (int) (Math.sqrt(grid.getGridSize().getSurfaceArea()) / 2);
		
		final boolean[] RowUsed = new boolean[grid.getGridSize().getHeight()];
		final boolean[] ColUsed = new boolean[grid.getGridSize().getWidth()];
		final boolean[] ValUsed = new boolean[grid.getGridSize().getAmountOfNumbers()];
		
		for (int i = 0; i < singles; i++) {
			GridCell cell;
			int cellIndex;
			do {
				cell = grid.getCell(RandomSingleton.getInstance()
						.nextInt(grid.getGridSize().getSurfaceArea()));
				
				if (cell.getValue() == GridCell.NO_VALUE_SET) {
					throw new RuntimeException("Found a cell without a value: " + grid.toString());
				}
				
				cellIndex = GameVariant.getInstance().getDigitSetting().indexOf(cell.getValue());
				
			} while (RowUsed[cell.getRow()] || ColUsed[cell.getColumn()] || ValUsed[cellIndex]);
			ColUsed[cell.getColumn()] = true;
			RowUsed[cell.getRow()] = true;
			ValUsed[cellIndex] = true;
			final GridCage cage = new GridCage(grid);
			cage.addCell(cell);
			cage.setSingleCellArithmetic();
			cage.setCageId(i);
			grid.addCage(cage);
		}
		return singles;
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
	
	private void calculateCageArithmetic(GridCage cage, final GridCageOperation operationSet) {
		GridCageOperationDecider decider = new GridCageOperationDecider(cage, operationSet);
		
		Optional<GridCageAction> operation = decider.decideOperation();
		
		if (operation.isPresent()) {
			cage.setAction(operation.get());
			cage.calculateResultFromAction();
		} else {
			cage.setSingleCellArithmetic();
		}
	}
}
