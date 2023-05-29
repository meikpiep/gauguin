package com.holokenmod.creation.cage

import com.holokenmod.grid.Grid
import kotlin.math.abs

class SubtractionCreator(
    private val grid: Grid,
    private val result: Int
) {
    fun create(): List<IntArray> {
        val possibles = mutableListOf<IntArray>()

        for (digit in grid.possibleDigits) {
            for (otherDigit in grid.possibleDigits) {
                if (abs(digit - otherDigit) == result) {
                    possibles += intArrayOf(digit, otherDigit)
                }
            }
        }

        return possibles
    }
}
