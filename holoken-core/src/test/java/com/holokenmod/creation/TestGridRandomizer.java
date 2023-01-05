package com.holokenmod.creation;

import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;

import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridCell;
import com.holokenmod.grid.GridSize;
import com.holokenmod.options.CurrentGameOptionsVariant;
import com.holokenmod.options.DigitSetting;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.stream.Stream;

public class TestGridRandomizer {
	
	@ParameterizedTest
	@MethodSource("gridSizeParameters")
	void testDigitsFromOneOn(int width, int heigth) {
		Assertions.assertTimeoutPreemptively(Duration.of(10, ChronoUnit.SECONDS), () -> {
			
			CurrentGameOptionsVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
			CurrentGameOptionsVariant.getInstance().setShowOperators(true);
			
			Grid grid = new Grid(new GridSize(width, heigth));
			
			grid.addAllCells();
			
			grid.clearUserValues();
			GridRandomizer randomizer = new GridRandomizer(new RandomPossibleDigitsShuffler(), grid);
			
			randomizer.createGrid();
			
			for(GridCell cell : grid.getCells()) {
				MatcherAssert.assertThat(
						grid.toString(),
						cell.getValue(),
						is(in(grid.getPossibleDigits())));
			}
		});
	}
	
	private static Stream<Arguments> gridSizeParameters() {
		ArrayList<Arguments> parameters = new ArrayList<>();
		
		for(int width = 3; width <= 11; width++) {
			for(int height = 3; height <= 11; height++) {
				parameters.add(Arguments.of(width, height));
			}
		}
		
		return parameters.stream();
	}
}