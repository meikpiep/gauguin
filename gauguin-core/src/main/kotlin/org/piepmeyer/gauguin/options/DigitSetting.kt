package org.piepmeyer.gauguin.options

import org.piepmeyer.gauguin.grid.GridSize

enum class DigitSetting(
    val numbers: Set<Int>,
) {
    FIRST_DIGIT_ONE((1..12).toSet()),
    FIRST_DIGIT_ZERO((0..11).toSet()),
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
        (-2..9).toSet(),
    ),
    FIRST_DIGIT_MINUS_FIVE((-5..6).toSet()),
    ;

    fun getPossibleDigits(gridSize: GridSize): Set<Int> = numbers.take(gridSize.amountOfNumbers).toSet()

    fun getMaximumDigit(gridSize: GridSize): Int = numbers.elementAt(gridSize.amountOfNumbers - 1)

    fun getPossibleNonZeroDigits(gridSize: GridSize): Set<Int> =
        getPossibleDigits(gridSize)
            .filterNot { it == 0 }
            .toSet()

    fun indexOf(value: Int): Int = numbers.indexOf(value)

    fun zeroOnKeyPadShouldBePlacedAtLast(): Boolean = numbers.first() == 0
}
