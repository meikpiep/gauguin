package com.holokenmod.creation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCageAction;
import com.holokenmod.GridSize;
import com.holokenmod.options.CurrentGameOptionsVariant;
import com.holokenmod.options.DigitSetting;

import org.junit.jupiter.api.Test;

class TestGridSingleCageCreator {

	@Test
	void getAllDivideResults() {
		CurrentGameOptionsVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
		
		Grid grid = new Grid(new GridSize(4, 4));
		GridCage cage = new GridCage(grid);
		cage.setResult(2);
		cage.setAction(GridCageAction.ACTION_DIVIDE);
		
		GridSingleCageCreator creator = new GridSingleCageCreator(grid, cage);
		
		assertThat(creator.getAllDivideResults().size(), is(4));
	}
	
	@Test
	void getAllSubtractResults() {
		CurrentGameOptionsVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
		CurrentGameOptionsVariant.getInstance().setShowOperators(true);
		
		Grid grid = new Grid(new GridSize(4, 4));
		GridCage cage = new GridCage(grid);
		cage.setResult(2);
		cage.setAction(GridCageAction.ACTION_SUBTRACT);
		
		GridSingleCageCreator creator = new GridSingleCageCreator(grid, cage);
		
		assertThat(creator.getPossibleNums().size(), is(4));
		assertThat(creator.getPossibleNums().get(0)[0], is(1));
		assertThat(creator.getPossibleNums().get(0)[1], is(3));
		assertThat(creator.getPossibleNums().get(1)[0], is(2));
		assertThat(creator.getPossibleNums().get(1)[1], is(4));
		assertThat(creator.getPossibleNums().get(2)[0], is(3));
		assertThat(creator.getPossibleNums().get(2)[1], is(1));
		assertThat(creator.getPossibleNums().get(3)[0], is(4));
		assertThat(creator.getPossibleNums().get(3)[1], is(2));
	}
}
