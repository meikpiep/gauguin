package com.holokenmod.creation.cage

import com.holokenmod.grid.Grid

class DivideCreator(
    private val grid: Grid,
    private val result: Int
) {
    fun create(): List<IntArray> {
        val results = mutableListOf<IntArray>()

        for (digit in grid.possibleDigits) {
            if (result == 0 || digit % result == 0) {
                val otherDigit: Int = if (result == 0) {
                    0
                } else {
                    digit / result
                }
                if (digit != otherDigit && grid.possibleDigits.contains(otherDigit)) {
                    results += intArrayOf(digit, otherDigit)
                    results += intArrayOf(otherDigit, digit)
                }
            }
        }

        return results
    }
}
