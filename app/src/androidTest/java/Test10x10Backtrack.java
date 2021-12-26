import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCageAction;
import com.holokenmod.backtrack.hybrid.MathDokuCage2BackTrack;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GameVariant;

import org.junit.jupiter.api.Test;

class Test10x10Backtrack {
	@Test
	void test9x9() {
		/*  |     1-  0 |     3x  1 |         1 |
		    |         0 |     4x  2 |         2 |
    		|     3/  3 |         3 |         2 | */
		
		GameVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
		GameVariant.getInstance().setShowOperators(true);
		
		Grid grid = new Grid(3);
		
		grid.addAllCells();
		
		GridCage cage = new GridCage(grid);
		cage.setCageId(0);
		cage.setAction(GridCageAction.ACTION_SUBTRACT);
		cage.setResult(1);
		cage.addCell(grid.getCell(0));
		cage.addCell(grid.getCell(3));
		grid.addCage(cage);

		cage = new GridCage(grid);
		cage.setCageId(1);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(3);
		cage.addCell(grid.getCell(1));
		cage.addCell(grid.getCell(2));
		grid.addCage(cage);
		
		cage = new GridCage(grid);
		cage.setCageId(2);
		cage.setAction(GridCageAction.ACTION_MULTIPLY);
		cage.setResult(4);
		cage.addCell(grid.getCell(4));
		cage.addCell(grid.getCell(5));
		cage.addCell(grid.getCell(8));
		grid.addCage(cage);
		
		cage = new GridCage(grid);
		cage.setCageId(3);
		cage.setAction(GridCageAction.ACTION_DIVIDE);
		cage.setResult(3);
		cage.addCell(grid.getCell(6));
		cage.addCell(grid.getCell(7));
		grid.addCage(cage);
		
		grid.setCageTexts();
		
		System.out.println(grid.toString());
		
		grid.clearUserValues();
		MathDokuCage2BackTrack backtrack = new MathDokuCage2BackTrack(grid, false);
		
		assertThat(backtrack.solve(), is(1));
	}
}