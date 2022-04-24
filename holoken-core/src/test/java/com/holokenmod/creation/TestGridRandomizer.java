package com.holokenmod.creation;

import com.holokenmod.Grid;
import com.holokenmod.GridSize;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GameVariant;

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
		GameVariant.getInstance().setDigitSetting(DigitSetting.PRIME_NUMBERS);
		GameVariant.getInstance().setShowOperators(true);
		
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
			
			GameVariant.getInstance().setDigitSetting(DigitSetting.FIRST_DIGIT_ONE);
			GameVariant.getInstance().setShowOperators(true);
			
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
		ArrayList<Arguments> paramters = new ArrayList<>();
		
		for(int width = 3; width <= 11; width++) {
			for(int height = 3; height <= 11; height++) {
				paramters.add(Arguments.of(width, height));
			}
		}
		
		return paramters.stream();
	}
}