package com.holokenmod.ui;

import java.util.ArrayList;
import java.util.Collection;

public enum DigitSetting {
    FIRST_DIGIT_ONE,
    FIRST_DIGIT_ZERO;


    public Collection<Integer> getPossibleDigits(int gridSize) {
        Collection<Integer> digits = new ArrayList<>();

        if (this == FIRST_DIGIT_ONE) {
            for (int i = 1; i <= gridSize; i++) {
                digits.add(i);
            }
        } else {
            for (int i = 0; i < gridSize; i++) {
                digits.add(i);
            }
        }

        return digits;
    }

    public int getMaximumDigit(int gridSize) {
        if (this == FIRST_DIGIT_ONE) {
            return gridSize;
        }

        return gridSize - 1;
    }
}