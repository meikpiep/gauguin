package org.piepmeyer.gauguin.options

import org.piepmeyer.gauguin.grid.GridSize

private fun allNumbersBetween(
    lowNumber: Int,
    highNumber: Int,
): Set<Int> {
    return (lowNumber..highNumber).toSet()
}

enum class DigitSetting(
    val numbers: Set<Int>,
) {
    FIRST_DIGIT_ONE(allNumbersBetween(1, 12)),
    FIRST_DIGIT_ZERO(allNumbersBetween(0, 11)),
    PRIME_NUMBERS(setOf(1, 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31)),
    FIBONACCI_SEQUENCE(
        setOf(
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
            233,
        ),
    ),
    PADOVAN_SEQUENCE(
        setOf(1, 2, 3, 4, 5, 7, 9, 12, 16, 21, 28, 37),
    ),
    FIRST_DIGIT_MINUS_TWO(
        allNumbersBetween(-2, 9),
    ),
    FIRST_DIGIT_MINUS_FIVE(allNumbersBetween(-5, 6)),
    ;

    fun getPossibleDigits(gridSize: GridSize): Set<Int> {
        return numbers.take(gridSize.amountOfNumbers).toSet()
    }

    fun getMaximumDigit(gridSize: GridSize): Int {
        return numbers.elementAt(gridSize.amountOfNumbers - 1)
    }

    fun getPossibleNonZeroDigits(gridSize: GridSize): Set<Int> {
        return getPossibleDigits(gridSize)
            .filterNot { it == 0 }
            .toSet()
    }

    fun indexOf(value: Int): Int {
        return numbers.indexOf(value)
    }
}
