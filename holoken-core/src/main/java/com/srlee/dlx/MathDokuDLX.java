package com.srlee.dlx;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.creation.GridSingleCageCreator;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GameVariant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class MathDokuDLX extends DLX {
	private final static Logger LOGGER = LoggerFactory.getLogger(MathDokuDLX.class);
	
	public MathDokuDLX(final Grid grid) {
		
		// Number of columns = number of constraints =
		//		BOARD * BOARD (for columns) +
		//		BOARD * BOARD (for rows)	+
		//		Num cages (each cage has to be filled once and only once)
		// Number of rows = number of "moves" =
		//		Sum of all the possible cage combinations
		// Number of nodes = sum of each move:
		//      num_cells column constraints +
		//      num_cells row constraints +
		//      1 (cage constraint)
		int total_nodes = 0;
		
		final Collection<GridSingleCageCreator> creators = new ArrayList<>();
		
		for (final GridCage cage : grid.getCages()) {
			creators.add(new GridSingleCageCreator(grid, cage));
		}
		
		for (final GridSingleCageCreator creator : creators) {
			total_nodes += creator.getPossibleNums().size() * (2 * creator.getNumberOfCells() + 1);
		}
		init(2 * grid.getGridSize().getSurfaceArea() + creators.size(), total_nodes);
		
		int constraint_num;
		int move_idx = 0;
		
		DigitSetting digitSetting = GameVariant.getInstance().getDigitSetting();
		
		for (final GridSingleCageCreator creator : creators) {
			for (final int[] onemove : creator.getPossibleNums()) {
				LOGGER.info("cage " + creator.getCage() + " - " + Arrays.toString(onemove));
				
				for (int i = 0; i < onemove.length; i++) {
					int numberToTestIndex = digitSetting.indexOf(onemove[i]);
					
					constraint_num = grid.getGridSize().getWidth() * numberToTestIndex + creator.getCell(i)
							.getColumn() + 1;
					addNode(constraint_num, move_idx);    // Column constraint
					constraint_num = grid.getGridSize().getSurfaceArea() + grid.getGridSize().getWidth() * numberToTestIndex + creator.getCell(i)
							.getRow() + 1;
					addNode(constraint_num, move_idx);    // Row constraint
				}
				constraint_num = 2 * grid.getGridSize().getSurfaceArea() + creator.getId() + 1;
				addNode(constraint_num, move_idx);    // Cage constraint
				move_idx++;
			}
		}
	}
}