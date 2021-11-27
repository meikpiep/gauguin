import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCageAction;
import com.holokenmod.backtrack.MathDokuCageBackTrack;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GameVariant;

import org.junit.jupiter.api.Test;

public class TestBacktrack {
	@Test
	void testFirstGrid() {
		/*  |     6x  0 |     4+  1 |     2/  2 |     0x  3 |
    		|         0 |         1 |         2 |         3 |
    		|     0x  4 |         1 |         3 |         3 |
    		|         4 |         4 |     3x  5 |         5 | */
		
		GameVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ZERO);
		GameVariant.getInstance().setShowOperators(true);
		
		Grid grid = new Grid(4);
		
		grid.addAllCells();
		
		GridCage cage = new GridCage(grid, 1);
		cage.setCageId(0);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(6);
		cage.addCell(grid.getCell(0));
		cage.addCell(grid.getCell(4));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 3);
		cage.setCageId(1);
		cage.setAction(GridCageAction.ACTION_ADD);
		cage.setResult(4);
		cage.addCell(grid.getCell(1));
		cage.addCell(grid.getCell(5));
		cage.addCell(grid.getCell(9));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 1);
		cage.setCageId(2);
		cage.setAction(GridCageAction.ACTION_DIVIDE);
		cage.setResult(2);
		cage.addCell(grid.getCell(2));
		cage.addCell(grid.getCell(6));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 11);
		cage.setCageId(3);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(0);
		cage.addCell(grid.getCell(3));
		cage.addCell(grid.getCell(7));
		cage.addCell(grid.getCell(10));
		cage.addCell(grid.getCell(11));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 5);
		cage.setCageId(4);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(0);
		cage.addCell(grid.getCell(8));
		cage.addCell(grid.getCell(12));
		cage.addCell(grid.getCell(13));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 2);
		cage.setCageId(5);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(3);
		cage.addCell(grid.getCell(14));
		cage.addCell(grid.getCell(15));
		grid.addCage(cage);
		
		grid.setCageTexts();
		
		System.out.println(grid.toString());
		
		MathDokuCageBackTrack backtrack = new MathDokuCageBackTrack(grid);
		
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
		
		Grid grid = new Grid(4);
		
		grid.addAllCells();
		
		GridCage cage = new GridCage(grid, 1);
		cage.setCageId(0);
		cage.setAction(GridCageAction.ACTION_SUBTRACT);
		cage.setResult(1);
		cage.addCell(grid.getCell(0));
		cage.addCell(grid.getCell(4));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 8);
		cage.setCageId(1);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(0);
		cage.addCell(grid.getCell(1));
		cage.addCell(grid.getCell(2));
		cage.addCell(grid.getCell(5));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 6);
		cage.setCageId(2);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(6);
		cage.addCell(grid.getCell(3));
		cage.addCell(grid.getCell(6));
		cage.addCell(grid.getCell(7));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 14);
		cage.setCageId(3);
		cage.setAction(GridCageAction.ACTION_ADD);
		cage.setResult(4);
		cage.addCell(grid.getCell(8));
		cage.addCell(grid.getCell(9));
		cage.addCell(grid.getCell(10));
		cage.addCell(grid.getCell(14));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 2);
		cage.setCageId(4);
		cage.setAction(GridCageAction.ACTION_SUBTRACT);
		cage.setResult(3);
		cage.addCell(grid.getCell(11));
		cage.addCell(grid.getCell(15));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 1);
		cage.setCageId(5);
		cage.setAction(GridCageAction.ACTION_ADD);
		cage.setResult(5);
		cage.addCell(grid.getCell(12));
		cage.addCell(grid.getCell(13));
		grid.addCage(cage);
		
		grid.setCageTexts();
		
		System.out.println(grid.toString());
		
		MathDokuCageBackTrack backtrack = new MathDokuCageBackTrack(grid);
		
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
		
		Grid grid = new Grid(4);
		
		grid.addAllCells();
		
		GridCage cage = new GridCage(grid, 10);
		cage.setCageId(0);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(12);
		cage.addCell(grid.getCell(0));
		cage.addCell(grid.getCell(1));
		cage.addCell(grid.getCell(4));
		cage.addCell(grid.getCell(8));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 1);
		cage.setCageId(1);
		cage.setAction(GridCageAction.ACTION_SUBTRACT);
		cage.setResult(1);
		cage.addCell(grid.getCell(2));
		cage.addCell(grid.getCell(6));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 3);
		cage.setCageId(2);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(0);
		cage.addCell(grid.getCell(3));
		cage.addCell(grid.getCell(7));
		cage.addCell(grid.getCell(11));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 5);
		cage.setCageId(3);
		cage.setAction(GridCageAction.ACTION_ADD);
		cage.setResult(4);
		cage.addCell(grid.getCell(5));
		cage.addCell(grid.getCell(9));
		cage.addCell(grid.getCell(12));
		cage.addCell(grid.getCell(13));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 11);
		cage.setCageId(4);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(12);
		cage.addCell(grid.getCell(10));
		cage.addCell(grid.getCell(14));
		cage.addCell(grid.getCell(15));
		grid.addCage(cage);
		
		grid.setCageTexts();
		
		System.out.println(grid.toString());
		
		MathDokuCageBackTrack backtrack = new MathDokuCageBackTrack(grid);
		
		assertThat(backtrack.solve(), is(2));
	}
	
	@Test
	void testAnotherGrid() {
		/*  |     3+  0 |     7+  1 |     3/  2 |         2 |
    		|         0 |         1 |         1 |     6+  3 |
    		|         0 |     1-  4 |         4 |         3 |
    		|     0x  5 |         5 |         3 |         3 | */
		
		GameVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ZERO);
		GameVariant.getInstance().setShowOperators(true);
		
		Grid grid = new Grid(4);
		
		grid.addAllCells();
		
		GridCage cage = new GridCage(grid, 3);
		cage.setCageId(0);
		cage.setAction(GridCageAction.ACTION_ADD);
		cage.setResult(3);
		cage.addCell(grid.getCell(0));
		cage.addCell(grid.getCell(4));
		cage.addCell(grid.getCell(8));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 5);
		cage.setCageId(1);
		cage.setAction(GridCageAction.ACTION_ADD);
		cage.setResult(7);
		cage.addCell(grid.getCell(1));
		cage.addCell(grid.getCell(5));
		cage.addCell(grid.getCell(6));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 2);
		cage.setCageId(2);
		cage.setAction(GridCageAction.ACTION_DIVIDE);
		cage.setResult(3);
		cage.addCell(grid.getCell(2));
		cage.addCell(grid.getCell(3));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 11);
		cage.setCageId(3);
		cage.setAction(GridCageAction.ACTION_ADD);
		cage.setResult(6);
		cage.addCell(grid.getCell(7));
		cage.addCell(grid.getCell(11));
		cage.addCell(grid.getCell(14));
		cage.addCell(grid.getCell(15));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 2);
		cage.setCageId(4);
		cage.setAction(GridCageAction.ACTION_SUBTRACT);
		cage.setResult(1);
		cage.addCell(grid.getCell(9));
		cage.addCell(grid.getCell(10));
		grid.addCage(cage);
		
		cage = new GridCage(grid, 2);
		cage.setCageId(5);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(0);
		cage.addCell(grid.getCell(12));
		cage.addCell(grid.getCell(13));
		grid.addCage(cage);
		
		grid.setCageTexts();
		
		System.out.println(grid.toString());
		
		MathDokuCageBackTrack backtrack = new MathDokuCageBackTrack(grid);
		
		assertThat(backtrack.solve(), is(1));
	}
}