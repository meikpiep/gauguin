package com.holokenmod.creation;

import com.holokenmod.grid.GridSize;
import com.holokenmod.options.DifficultySetting;
import com.holokenmod.options.GameOptionsVariant;
import com.holokenmod.options.GameVariant;

import org.junit.jupiter.api.Test;

public class TestGridCreatorPerformance {
	
	@Test
	public void test() {
		GameVariant variant = new GameVariant(
				new GridSize(9, 9),
				GameOptionsVariant.createClassic());
		
		variant.getOptions().setDifficultySetting(DifficultySetting.EXTREME);
		
		GridCreator creator = new GridCreator(
				variant);
		
		creator.createRandomizedGridWithCages();
	}
}
