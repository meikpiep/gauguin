package com.holokenmod.options;

import com.holokenmod.GridSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum DigitSetting {
	FIRST_DIGIT_ONE(allNumbersBetween(1, 11)),
	FIRST_DIGIT_ZERO(allNumbersBetween(0, 10)),
	PRIME_NUMBERS(Arrays.asList(1, 2, 3, 5, 7, 9, 11, 13, 17, 19, 23));
	
	private final List<Integer> numbers;
	
	DigitSetting(List<Integer> numbers) {
		this.numbers = numbers;
	}
	
	private static List<Integer> allNumbersBetween(int lowNumber, int highNumber) {
		ArrayList<Integer> numbers = new ArrayList<>();
		
		for(int i = lowNumber; i <= highNumber; i++) {
			numbers.add(i);
		}
		
		return numbers;
	}
	
	public Collection<Integer> getPossibleDigits(final GridSize gridSize) {
		final Collection<Integer> digits = new ArrayList<>();
		
		for (int i = 0; i < gridSize.getAmountOfNumbers(); i++) {
			digits.add(numbers.get(i));
		}
		
		return digits;
	}
	
	public int getMaximumDigit(final GridSize gridSize) {
		return numbers.get(gridSize.getAmountOfNumbers() - 1);
	}
	
	public Collection<Integer> getPossibleNonZeroDigits(final GridSize gridSize) {
		if (numbers.get(0) == 0) {
			return numbers.subList(1, gridSize.getAmountOfNumbers() + 1);
		}
		
		return numbers.subList(0, gridSize.getAmountOfNumbers());
	}
	
	public boolean containsZero() {
		return this != FIRST_DIGIT_ONE;
	}
	
	public int indexOf(int value) {
		return this.numbers.indexOf(value);
	}
}