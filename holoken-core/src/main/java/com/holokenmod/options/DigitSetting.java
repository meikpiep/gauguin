package com.holokenmod.options;

import com.holokenmod.GridSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum DigitSetting {
	FIRST_DIGIT_ONE(allNumbersBetween(1, 12)),
	FIRST_DIGIT_ZERO(allNumbersBetween(0, 11)),
	PRIME_NUMBERS(Arrays.asList(1, 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31)),
	FIBONACCI_SEQUENCE(Arrays.asList(1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233)),
	PADOVAN_SEQUENCE(Arrays.asList(1, 2, 3, 4, 5, 7, 9, 12, 16, 21, 28, 37)),
	FIRST_DIGIT_MINUS_TWO(allNumbersBetween(-2, 9)),
	FIRST_DIGIT_MINUS_FIVE(allNumbersBetween(-5, 6));
	
	private final List<Integer> numbers;
	
	DigitSetting(List<Integer> numbers) {
		this.numbers = Collections.unmodifiableList(numbers);
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
		return numbers.stream()
				.limit(gridSize.getAmountOfNumbers())
				.filter(i -> i != 0)
				.collect(Collectors.toList());
	}
	
	public boolean containsZero() {
		return this == FIRST_DIGIT_ZERO;
	}
	
	public int indexOf(int value) {
		return this.numbers.indexOf(value);
	}
	
	public List<Integer> getAllNumbers() {
		return numbers;
	}
}