package com.holokenmod.creation;

import com.holokenmod.Grid;
import com.holokenmod.GridSize;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GameVariant;
import com.holokenmod.options.GridCageOperation;
import com.holokenmod.options.SingleCageUsage;

import org.junit.jupiter.api.RepeatedTest;

public class TestGridDifficulty {
	@RepeatedTest(20)
	void testDifficulty() {
		GameVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
		GameVariant.getInstance().setShowOperators(true);
		GameVariant.getInstance().setSingleCageUsage(SingleCageUsage.FIXED_NUMBER);
		GameVariant.getInstance().setCageOperation(GridCageOperation.OPERATIONS_ALL);
		
		GridCreator creator = new GridCreator(new GridSize(9, 9));
		
		Grid grid = creator.createRandomizedGridWithCages();
		
		System.out.println(new GridDifficulty(grid).calculate());
	}
}
