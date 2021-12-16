package com.holokenmod.options;

import com.holokenmod.GridSize;

import java.util.ArrayList;
import java.util.Collection;

public enum DigitSetting {
	FIRST_DIGIT_ONE,
	FIRST_DIGIT_ZERO;
	
	public Collection<Integer> getPossibleDigits(final GridSize gridSize) {
		final Collection<Integer> digits = new ArrayList<>();
		
		for (int i = getMinimumDigit(); i <= getMaximumDigit(gridSize); i++) {
			digits.add(i);
		}
		
		return digits;
	}
	
	public int getMaximumDigit(final GridSize gridSize) {
		if (this == FIRST_DIGIT_ONE) {
			return gridSize.getAmountOfNumbers();
		}
		
		return gridSize.getAmountOfNumbers() - 1;
	}
	
	public int getMinimumDigit() {
		if (this == FIRST_DIGIT_ONE) {
			return 1;
		}
		
		return 0;
	}
	
	public Collection<Integer> getPossibleNonZeroDigits(final GridSize gridSize) {
		final Collection<Integer> digits = new ArrayList<>();
		
		for (int i = 1; i <= getMaximumDigit(gridSize); i++) {
			digits.add(i);
		}
		
		return digits;
	}
}