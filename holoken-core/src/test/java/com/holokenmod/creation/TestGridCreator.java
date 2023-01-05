package com.holokenmod.creation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridSize;
import com.holokenmod.options.CurrentGameOptionsVariant;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GridCageOperation;
import com.holokenmod.options.SingleCageUsage;

import org.junit.jupiter.api.Test;

public class TestGridCreator {
	@Test
	void test3x3GridCreationWithoutRandomValues() {
		CurrentGameOptionsVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
		CurrentGameOptionsVariant.getInstance().setShowOperators(true);
		CurrentGameOptionsVariant.getInstance().setSingleCageUsage(SingleCageUsage.FIXED_NUMBER);
		CurrentGameOptionsVariant.getInstance().setCageOperation(GridCageOperation.OPERATIONS_ALL);
		
		GridCreator creator = new GridCreator(
				new RandomizerMock(),
				new ShufflerStub(),
				new GridSize(3, 3));
		
		Grid grid = creator.createRandomizedGridWithCages();
		
		assertThat(grid.getCellAt(0, 0).getValue(), is(1));
		assertThat(grid.getCellAt(0, 1).getValue(), is(2));
		assertThat(grid.getCellAt(0, 2).getValue(), is(3));
		
		assertThat(grid.getCellAt(1, 0).getValue(), is(2));
		assertThat(grid.getCellAt(1, 1).getValue(), is(3));
		assertThat(grid.getCellAt(1, 2).getValue(), is(1));
		
		assertThat(grid.getCellAt(2, 0).getValue(), is(3));
		assertThat(grid.getCellAt(2, 1).getValue(), is(1));
		assertThat(grid.getCellAt(2, 2).getValue(), is(2));
	}
}
