package com.holokenmod.creation.cage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridCage;
import com.holokenmod.grid.GridCageAction;
import com.holokenmod.grid.GridSize;
import com.holokenmod.options.CurrentGameOptionsVariant;
import com.holokenmod.options.DigitSetting;

import org.junit.jupiter.api.Test;

class TestGridSingleCageCreator {

	@Test
	void getAllDivideResultsWithoutZero() {
		CurrentGameOptionsVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
		
		Grid grid = new Grid(new GridSize(4, 4));
		GridCage cage = new GridCage(grid);
		cage.setResult(2);
		cage.setAction(GridCageAction.ACTION_DIVIDE);
		
		GridSingleCageCreator creator = new GridSingleCageCreator(grid, cage);
		
		assertThat(creator.getAllDivideResults().size(), is(4));
		assertThat(creator.getPossibleNums().get(0)[0], is(2));
		assertThat(creator.getPossibleNums().get(0)[1], is(1));
		assertThat(creator.getPossibleNums().get(1)[0], is(1));
		assertThat(creator.getPossibleNums().get(1)[1], is(2));
		assertThat(creator.getPossibleNums().get(2)[0], is(4));
		assertThat(creator.getPossibleNums().get(2)[1], is(2));
		assertThat(creator.getPossibleNums().get(3)[0], is(2));
		assertThat(creator.getPossibleNums().get(3)[1], is(4));
	}
	
	@Test
	void getAllDivideResultsWithZero() {
		CurrentGameOptionsVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ZERO);
		
		Grid grid = new Grid(new GridSize(4, 4));
		GridCage cage = new GridCage(grid);
		cage.setResult(0);
		cage.setAction(GridCageAction.ACTION_DIVIDE);
		
		GridSingleCageCreator creator = new GridSingleCageCreator(grid, cage);
		
		assertThat(creator.getAllDivideResults().size(), is(6));
		assertThat(creator.getPossibleNums().get(0)[0], is(1));
		assertThat(creator.getPossibleNums().get(0)[1], is(0));
		assertThat(creator.getPossibleNums().get(1)[0], is(0));
		assertThat(creator.getPossibleNums().get(1)[1], is(1));
		assertThat(creator.getPossibleNums().get(2)[0], is(2));
		assertThat(creator.getPossibleNums().get(2)[1], is(0));
		assertThat(creator.getPossibleNums().get(3)[0], is(0));
		assertThat(creator.getPossibleNums().get(3)[1], is(2));
		assertThat(creator.getPossibleNums().get(4)[0], is(3));
		assertThat(creator.getPossibleNums().get(4)[1], is(0));
		assertThat(creator.getPossibleNums().get(5)[0], is(0));
		assertThat(creator.getPossibleNums().get(5)[1], is(3));
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
