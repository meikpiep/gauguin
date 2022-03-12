package com.holokenmod.creation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCageAction;
import com.holokenmod.GridSize;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GameVariant;

import org.junit.jupiter.api.Test;

public class TestGridSingleCageCreator {

	@Test
	void getAllDivideResults() {
		GameVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
		
		Grid grid = new Grid(new GridSize(4, 4));
		GridCage cage = new GridCage(grid);
		cage.setResult(2);
		cage.setAction(GridCageAction.ACTION_DIVIDE);
		
		GridSingleCageCreator creator = new GridSingleCageCreator(grid, cage);
		
		assertThat(creator.getAllDivideResults().size(), is(4));
	}
}
