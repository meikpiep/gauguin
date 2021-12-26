import com.holokenmod.Grid;
import com.holokenmod.GridSize;
import com.holokenmod.creation.GridRandomizer;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GameVariant;

import org.junit.jupiter.api.Test;

public class TestGridRandomizer {
	@Test
	void test() {
		GameVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
		GameVariant.getInstance().setShowOperators(true);
		
		Grid grid = new Grid(new GridSize(6, 3));
		
		grid.addAllCells();
		
		System.out.println(grid.toString());
		
		grid.clearUserValues();
		GridRandomizer randomizer = new GridRandomizer(grid);
		
		randomizer.createGrid();
		
		System.out.println(grid.toString());
	}
}