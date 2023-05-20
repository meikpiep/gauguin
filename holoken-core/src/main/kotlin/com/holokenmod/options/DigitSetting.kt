package com.holokenmod.options

import com.holokenmod.grid.GridSize
import java.util.Collections
import java.util.stream.Collectors

private fun allNumbersBetween(lowNumber: Int, highNumber: Int): List<Int> {
    val numbers = ArrayList<Int>()
    for (i in lowNumber..highNumber) {
        numbers.add(i)
    }
    return numbers
}

enum class DigitSetting(numbers: List<Int>) {
    FIRST_DIGIT_ONE(allNumbersBetween(1, 12)),
    FIRST_DIGIT_ZERO(allNumbersBetween(0, 11)),
    PRIME_NUMBERS(listOf(1, 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31)),
    FIBONACCI_SEQUENCE(
        listOf(
            1,
            2,
            3,
            5,
            8,
            13,
            21,
            34,
            55,
            89,
            144,
            233
        )
    ),
    PADOVAN_SEQUENCE(
        listOf(1, 2, 3, 4, 5, 7, 9, 12, 16, 21, 28, 37)
    ),
    FIRST_DIGIT_MINUS_TWO(
        allNumbersBetween(-2, 9)
    ),
    FIRST_DIGIT_MINUS_FIVE(allNumbersBetween(-5, 6));

    val allNumbers: List<Int>

    init {
        allNumbers = Collections.unmodifiableList(numbers)
    }

    fun getPossibleDigits(gridSize: GridSize): List<Int> {
        var digits = mutableListOf<Int>()
        for (i in 0 until gridSize.amountOfNumbers) {
            digits = (digits + allNumbers[i]) as MutableList<Int>
        }
        return digits
    }

    fun getMaximumDigit(gridSize: GridSize): Int {
        return allNumbers[gridSize.amountOfNumbers - 1]
    }

    fun getPossibleNonZeroDigits(gridSize: GridSize): Collection<Int> {
        return allNumbers.stream()
            .limit(gridSize.amountOfNumbers.toLong())
            .filter { i: Int -> i != 0 }
            .collect(Collectors.toList())
    }

    fun containsZero(): Boolean {
        return this == FIRST_DIGIT_ZERO
    }

    fun indexOf(value: Int): Int {
        return allNumbers.indexOf(value)
    }
}
