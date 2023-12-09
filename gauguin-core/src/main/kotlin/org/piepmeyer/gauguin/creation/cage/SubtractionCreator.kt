package org.piepmeyer.gauguin.creation.cage

import org.piepmeyer.gauguin.options.GameVariant
import kotlin.math.abs

class SubtractionCreator(
    private val variant: GameVariant,
    private val result: Int,
) {
    fun create(): List<IntArray> {
        val possibles = mutableListOf<IntArray>()

        for (digit in variant.possibleDigits) {
            for (otherDigit in variant.possibleDigits) {
                if (abs(digit - otherDigit) == result) {
                    possibles += intArrayOf(digit, otherDigit)
                }
            }
        }

        return possibles
    }
}
