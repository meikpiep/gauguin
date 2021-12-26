import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCageAction;
import com.holokenmod.backtrack.hybrid.MathDokuCage2BackTrack;
import com.holokenmod.creation.GridBuilder;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GameVariant;

import org.junit.jupiter.api.Test;

public class TestCageBacktrack {
	@Test
	void testFirstGrid3x3() {
		/*  |     1-  0 |     3x  1 |         1 |
		    |         0 |     4x  2 |         2 |
    		|     3/  3 |         3 |         2 | */
		
		GameVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
		GameVariant.getInstance().setShowOperators(true);
		
		GridBuilder builder = new GridBuilder(3, 3);
		
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
		
		GameVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
		GameVariant.getInstance().setShowOperators(true);
		
		Grid grid = new Grid(3);
		
		grid.addAllCells();
		
		GridCage cage = new GridCage(grid);
		cage.setCageId(0);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(3);
		cage.addCell(grid.getCell(0));
		cage.addCell(grid.getCell(1));
		cage.addCell(grid.getCell(4));
		grid.addCage(cage);
		
		cage = new GridCage(grid);
		cage.setCageId(1);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(12);
		cage.addCell(grid.getCell(2));
		cage.addCell(grid.getCell(5));
		cage.addCell(grid.getCell(7));
		cage.addCell(grid.getCell(8));
		grid.addCage(cage);
		
		cage = new GridCage(grid);
		cage.setCageId(2);
		cage.setAction(GridCageAction.ACTION_ADD);
		cage.setResult(5);
		cage.addCell(grid.getCell(3));
		cage.addCell(grid.getCell(6));
		grid.addCage(cage);
		
		grid.setCageTexts();
		
		System.out.println(grid.toString());
		
		grid.clearUserValues();
		MathDokuCage2BackTrack backtrack = new MathDokuCage2BackTrack(grid, false);
		
		assertThat(backtrack.solve(), is(1));
	}
	
	@Test
	void testGrid4x4() {
		/*      |     2/  0 |         0 |     3+  1 |         1 |
    			|     0x  2 |     6+  3 |         3 |         3 |
   			 	|         2 |         2 |     6+  4 |         3 |
    			|     3-  5 |         5 |         4 |         4 |*/
		
		GameVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ZERO);
		GameVariant.getInstance().setShowOperators(true);
		
		Grid grid = new Grid(4);
		
		grid.addAllCells();
		
		GridCage cage = new GridCage(grid, GridCageAction.ACTION_DIVIDE, 2);
		cage.addCellNumbers(0, 1);
		grid.addCage(cage);
		
		cage = new GridCage(grid, GridCageAction.ACTION_ADD, 3);
		cage.addCellNumbers(2, 3);
		grid.addCage(cage);
		
		cage = new GridCage(grid, GridCageAction.ACTION_MULTIPLY, 0);
		cage.addCellNumbers(4, 8, 9);
		grid.addCage(cage);
		
		cage = new GridCage(grid, GridCageAction.ACTION_ADD, 6);
		cage.addCellNumbers(5, 6, 7, 11);
		grid.addCage(cage);
		
		cage = new GridCage(grid, GridCageAction.ACTION_ADD, 6);
		cage.addCellNumbers(10, 14, 15);
		grid.addCage(cage);
		
		cage = new GridCage(grid, GridCageAction.ACTION_SUBTRACT, 3);
		cage.addCellNumbers(12, 13);
		grid.addCage(cage);
		
		grid.setCageTexts();
		
		System.out.println(grid.toString());
		
		grid.clearUserValues();
		MathDokuCage2BackTrack backtrack = new MathDokuCage2BackTrack(grid, false);
		
		assertThat(backtrack.solve(), is(2));
	}
}