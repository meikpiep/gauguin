package com.holokenmod.backtrack.hybrid;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridCageAction;
import com.holokenmod.creation.GridBuilder;
import com.holokenmod.options.CurrentGameOptionsVariant;
import com.holokenmod.options.DigitSetting;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TestCageBacktrack {
	@Disabled
	@Test
	void testFirstGrid3x3() {
		/*  |     1-  0 |     3x  1 |         1 |
		    |         0 |     4x  2 |         2 |
    		|     3/  3 |         3 |         2 | */
		
		CurrentGameOptionsVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
		CurrentGameOptionsVariant.getInstance().setShowOperators(true);
		
		GridBuilder builder = new GridBuilder(3);
		
		builder.addCage(1, GridCageAction.ACTION_SUBTRACT, 0, 3)
			.addCage(3, GridCageAction.ACTION_MULTIPLY, 1, 2)
			.addCage(4, GridCageAction.ACTION_MULTIPLY, 4, 5, 8)
			.addCage(3, GridCageAction.ACTION_DIVIDE, 6, 7);

		Grid grid = builder.createGrid();
		
		System.out.println(grid.toString());
		
		grid.clearUserValues();
		MathDokuCage2BackTrack backtrack = new MathDokuCage2BackTrack(grid, false);
		
		assertThat(backtrack.solve(), is(1));
	}
	
	@Test
	void testSecondGrid3x3() {
		/*      |     3x  0 |         0 |    12x  1 |
    			|     5+  2 |         0 |         1 |
   				|         2 |         1 |         1 | */
		
		CurrentGameOptionsVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
		CurrentGameOptionsVariant.getInstance().setShowOperators(true);
		
		GridBuilder builder = new GridBuilder(3);
		
		builder.addCage(3, GridCageAction.ACTION_MULTIPLY, 0, 1, 4)
				.addCage(12, GridCageAction.ACTION_MULTIPLY, 2, 5, 7, 8)
				.addCage(5, GridCageAction.ACTION_ADD, 3, 6);
		
		Grid grid = builder.createGrid();
		
		System.out.println(grid.toString());
		
		grid.clearUserValues();
		MathDokuCage2BackTrack backtrack = new MathDokuCage2BackTrack(grid, false);
		
		assertThat(backtrack.solve(), is(1));
	}
	
	@Test
	void testThirdGrid3x3() {
		/*  |     6+  0 |     7+  1 |         1 |
    		|         0 |         1 |         1 |
    		|         0 |     6x  2 |         2 | */
		
		CurrentGameOptionsVariant.getInstance().setDigitSetting(DigitSetting.PRIME_NUMBERS);
		CurrentGameOptionsVariant.getInstance().setShowOperators(true);
		
		GridBuilder builder = new GridBuilder(3);
		
		builder.addCage(6, GridCageAction.ACTION_ADD, 0, 3, 6)
				.addCage(7, GridCageAction.ACTION_ADD, 1, 2, 4, 5)
				.addCage(6, GridCageAction.ACTION_MULTIPLY, 7, 8);
		
		Grid grid = builder.createGrid();
		
		System.out.println(grid.toString());
		
		grid.clearUserValues();
		MathDokuCage2BackTrack backtrack = new MathDokuCage2BackTrack(grid, false);
		
		assertThat(backtrack.solve(), is(2));
	}
	
	@Test
	void testGrid4x4() {
		/*      |     2/  0 |         0 |     3+  1 |         1 |
    			|     0x  2 |     6+  3 |         3 |         3 |
   			 	|         2 |         2 |     6+  4 |         3 |
    			|     3-  5 |         5 |         4 |         4 |*/
		
		CurrentGameOptionsVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ZERO);
		CurrentGameOptionsVariant.getInstance().setShowOperators(true);
		
		GridBuilder builder = new GridBuilder(4);
		
		builder.addCage(2, GridCageAction.ACTION_DIVIDE, 0, 1)
				.addCage(3, GridCageAction.ACTION_ADD, 2, 3)
				.addCage(0, GridCageAction.ACTION_MULTIPLY, 4, 8, 9)
				.addCage(6, GridCageAction.ACTION_ADD, 5, 6, 7, 11)
				.addCage(6, GridCageAction.ACTION_ADD, 10, 14, 15)
				.addCage(3, GridCageAction.ACTION_SUBTRACT, 12, 13);
		
		Grid grid = builder.createGrid();
		
		System.out.println(grid.toString());
		
		grid.clearUserValues();
		MathDokuCage2BackTrack backtrack = new MathDokuCage2BackTrack(grid, false);
		
		assertThat(backtrack.solve(), is(2));
	}
}