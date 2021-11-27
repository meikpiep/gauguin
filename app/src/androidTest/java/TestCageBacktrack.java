import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCageAction;
import com.holokenmod.backtrack.MathDokuCageBackTrack;
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
		
		Grid grid = new Grid(3);
		
		grid.addAllCells();
		
		GridCage cage = new GridCage(grid, 1);
		cage.setCageId(0);
		cage.setAction(GridCageAction.ACTION_SUBTRACT);
		cage.setResult(1);
		cage.addCell(grid.getCell(0));
		cage.addCell(grid.getCell(3));
		grid.addCage(cage);

		cage = new GridCage(grid, 2);
		cage.setCageId(1);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(3);
		cage.addCell(grid.getCell(1));
		cage.addCell(grid.getCell(2));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 7);
		cage.setCageId(2);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(4);
		cage.addCell(grid.getCell(4));
		cage.addCell(grid.getCell(5));
		cage.addCell(grid.getCell(8));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 2);
		cage.setCageId(3);
		cage.setAction(GridCageAction.ACTION_DIVIDE);
		cage.setResult(3);
		cage.addCell(grid.getCell(6));
		cage.addCell(grid.getCell(7));
		grid.addCage(cage);
		
		grid.setCageTexts();
		
		System.out.println(grid.toString());
		
		//MathDokuDLX dlx = new MathDokuDLX(grid);
		//dlx.Solve(DLX.SolveType.MULTIPLE);
		
		grid.clearUserValues();
		MathDokuCageBackTrack backtrack = new MathDokuCageBackTrack(grid);
		
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
		
		GridCage cage = new GridCage(grid, 7);
		cage.setCageId(0);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(3);
		cage.addCell(grid.getCell(0));
		cage.addCell(grid.getCell(1));
		cage.addCell(grid.getCell(4));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 11);
		cage.setCageId(1);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(12);
		cage.addCell(grid.getCell(2));
		cage.addCell(grid.getCell(5));
		cage.addCell(grid.getCell(7));
		cage.addCell(grid.getCell(8));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 1);
		cage.setCageId(2);
		cage.setAction(GridCageAction.ACTION_ADD);
		cage.setResult(5);
		cage.addCell(grid.getCell(3));
		cage.addCell(grid.getCell(6));
		grid.addCage(cage);
		
		grid.setCageTexts();
		
		System.out.println(grid.toString());
		
		//MathDokuDLX dlx = new MathDokuDLX(grid);
		//dlx.Solve(DLX.SolveType.MULTIPLE);
		
		grid.clearUserValues();
		MathDokuCageBackTrack backtrack = new MathDokuCageBackTrack(grid);
		
		assertThat(backtrack.solve(), is(1));
	}
}