package com.holokenmod.creation;

import com.holokenmod.Grid;
import com.holokenmod.GridSize;
import com.holokenmod.options.CurrentGameOptionsVariant;
import com.holokenmod.options.DigitSetting;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.stream.Stream;

public class TestGridRandomizer {
	@Test
	void testPrimeNumbers() {
		CurrentGameOptionsVariant.getInstance().setDigitSetting(DigitSetting.PRIME_NUMBERS);
		CurrentGameOptionsVariant.getInstance().setShowOperators(true);
		
		Grid grid = new Grid(new GridSize(6, 3));
		
		grid.addAllCells();
		
		System.out.println(grid.toString());
		
		grid.clearUserValues();
		GridRandomizer randomizer = new GridRandomizer(grid);
		
		randomizer.createGrid();
		
		System.out.println(grid.toString());
	}
	
	@ParameterizedTest
	@MethodSource("gridSizeParameters")
	void testDigitsFromOneOn(int width, int heigth) {
		Assertions.assertTimeoutPreemptively(Duration.of(10, ChronoUnit.SECONDS), () -> {
			
			CurrentGameOptionsVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
			CurrentGameOptionsVariant.getInstance().setShowOperators(true);
			
			Grid grid = new Grid(new GridSize(width, heigth));
			
			grid.addAllCells();
			
			System.out.println(grid.toString());
			
			grid.clearUserValues();
			GridRandomizer randomizer = new GridRandomizer(grid);
			
			randomizer.createGrid();
			
			System.out.println(grid.toString());
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