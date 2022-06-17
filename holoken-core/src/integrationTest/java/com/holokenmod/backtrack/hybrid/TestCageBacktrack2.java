package com.holokenmod.backtrack.hybrid;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCageAction;
import com.holokenmod.GridSize;
import com.holokenmod.creation.GridBuilder;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GameVariant;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TestCageBacktrack2 {
	@Test
	void testFirstGrid() {
		/*  |     6x  0 |     4+  1 |     2/  2 |     0x  3 |
    		|         0 |         1 |         2 |         3 |
    		|     0x  4 |         1 |         3 |         3 |
    		|         4 |         4 |     3x  5 |         5 | */
		
		GameVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ZERO);
		GameVariant.getInstance().setShowOperators(true);
		
		GridBuilder builder = new GridBuilder(4);
		
		builder.addCage(6, GridCageAction.ACTION_MULTIPLY, 0, 4)
				.addCage(4, GridCageAction.ACTION_ADD, 1, 5, 9)
				.addCage(2, GridCageAction.ACTION_DIVIDE, 2, 6)
				.addCage(0, GridCageAction.ACTION_MULTIPLY, 3, 7, 10, 11)
				.addCage(0, GridCageAction.ACTION_MULTIPLY, 8, 12, 13)
				.addCage(3, GridCageAction.ACTION_MULTIPLY, 14, 15);
		
		Grid grid = builder.createGrid();
		
		System.out.println(grid.toString());
		
		MathDokuCage2BackTrack backtrack = new MathDokuCage2BackTrack(grid, false);
		
		assertThat(backtrack.solve(), is(2));
	}
	
	@Test
	void testSecondGrid() {
		/*  |     1-  0 |     0x  1 |         1 |     6x  2 |
    		|         0 |         1 |         2 |         2 |
    		|     4+  3 |         3 |         3 |     3-  4 |
    		|     5+  5 |         5 |         3 |         4 |*/
		
		GameVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ZERO);
		GameVariant.getInstance().setShowOperators(true);
		
		GridBuilder builder = new GridBuilder(4);
		
		builder.addCage(1, GridCageAction.ACTION_SUBTRACT, 0, 4)
				.addCage(0, GridCageAction.ACTION_MULTIPLY, 1, 2, 5)
				.addCage(6, GridCageAction.ACTION_MULTIPLY, 3, 6, 7)
				.addCage(4, GridCageAction.ACTION_ADD, 8, 9, 10, 14)
				.addCage(3, GridCageAction.ACTION_SUBTRACT, 11, 15)
				.addCage(5, GridCageAction.ACTION_ADD, 12, 13);
		
		Grid grid = builder.createGrid();
		
		System.out.println(grid.toString());
		
		MathDokuCage2BackTrack backtrack = new MathDokuCage2BackTrack(grid, false);
		
		assertThat(backtrack.solve(), is(2));
	}
	
	@Test
	void testThirdGrid() {
		/*  |    12x  0 |         0 |     1-  1 |     0x  2 |
    		|         0 |     4+  3 |         1 |         2 |
    		|         0 |         3 |    12x  4 |         2 |
    		|         3 |         3 |         4 |         4 |*/
		
		GameVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ZERO);
		GameVariant.getInstance().setShowOperators(true);
		
		Grid grid = new Grid(new GridSize(4, 4));
		
		grid.addAllCells();
		
		GridCage cage = new GridCage(grid);
		cage.setCageId(0);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(12);
		cage.addCell(grid.getCell(0));
		cage.addCell(grid.getCell(1));
		cage.addCell(grid.getCell(4));
		cage.addCell(grid.getCell(8));
		grid.addCage(cage);
		
		cage = new GridCage(grid);
		cage.setCageId(1);
		cage.setAction(GridCageAction.ACTION_SUBTRACT);
		cage.setResult(1);
		cage.addCell(grid.getCell(2));
		cage.addCell(grid.getCell(6));
		grid.addCage(cage);
		
		cage = new GridCage(grid);
		cage.setCageId(2);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(0);
		cage.addCell(grid.getCell(3));
		cage.addCell(grid.getCell(7));
		cage.addCell(grid.getCell(11));
		grid.addCage(cage);
		
		cage = new GridCage(grid);
		cage.setCageId(3);
		cage.setAction(GridCageAction.ACTION_ADD);
		cage.setResult(4);
		cage.addCell(grid.getCell(5));
		cage.addCell(grid.getCell(9));
		cage.addCell(grid.getCell(12));
		cage.addCell(grid.getCell(13));
		grid.addCage(cage);
		
		cage = new GridCage(grid);
		cage.setCageId(4);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(12);
		cage.addCell(grid.getCell(10));
		cage.addCell(grid.getCell(14));
		cage.addCell(grid.getCell(15));
		grid.addCage(cage);
		
		grid.setCageTexts();
		
		System.out.println(grid.toString());
		
		MathDokuCage2BackTrack backtrack = new MathDokuCage2BackTrack(grid, false);
		
		assertThat(backtrack.solve(), is(2));
	}
	
	@Disabled
	@Test
	void testAnotherGrid() {
		/*  |     3+  0 |     7+  1 |     3/  2 |         2 |
    		|         0 |         1 |         1 |     6+  3 |
    		|         0 |     1-  4 |         4 |         3 |
    		|     0x  5 |         5 |         3 |         3 | */
		
		GameVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ZERO);
		GameVariant.getInstance().setShowOperators(true);
		
		Grid grid = new Grid(new GridSize(4, 4));
		
		grid.addAllCells();
		
		GridCage cage = new GridCage(grid);
		cage.setCageId(0);
		cage.setAction(GridCageAction.ACTION_ADD);
		cage.setResult(3);
		cage.addCell(grid.getCell(0));
		cage.addCell(grid.getCell(4));
		cage.addCell(grid.getCell(8));
		grid.addCage(cage);
		
		cage = new GridCage(grid);
		cage.setCageId(1);
		cage.setAction(GridCageAction.ACTION_ADD);
		cage.setResult(7);
		cage.addCell(grid.getCell(1));
		cage.addCell(grid.getCell(5));
		cage.addCell(grid.getCell(6));
		grid.addCage(cage);
		
		cage = new GridCage(grid);
		cage.setCageId(2);
		cage.setAction(GridCageAction.ACTION_DIVIDE);
		cage.setResult(3);
		cage.addCell(grid.getCell(2));
		cage.addCell(grid.getCell(3));
		grid.addCage(cage);
		
		cage = new GridCage(grid);
		cage.setCageId(3);
		cage.setAction(GridCageAction.ACTION_ADD);
		cage.setResult(6);
		cage.addCell(grid.getCell(7));
		cage.addCell(grid.getCell(11));
		cage.addCell(grid.getCell(14));
		cage.addCell(grid.getCell(15));
		grid.addCage(cage);
		
		cage = new GridCage(grid);
		cage.setCageId(4);
		cage.setAction(GridCageAction.ACTION_SUBTRACT);
		cage.setResult(1);
		cage.addCell(grid.getCell(9));
		cage.addCell(grid.getCell(10));
		grid.addCage(cage);
		
		cage = new GridCage(grid);
		cage.setCageId(5);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(0);
		cage.addCell(grid.getCell(12));
		cage.addCell(grid.getCell(13));
		grid.addCage(cage);
		
		grid.setCageTexts();
		
		System.out.println(grid.toString());
		
		MathDokuCage2BackTrack backtrack = new MathDokuCage2BackTrack(grid, false);
		
		assertThat(backtrack.solve(), is(1));
	}
}