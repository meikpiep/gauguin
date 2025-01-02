package org.piepmeyer.gauguin.creation.cage.operation

import org.piepmeyer.gauguin.options.GameVariant

class DivideCreator(
    private val variant: GameVariant,
    private val result: Int,
) {
    fun create(): Set<IntArray> {
        val results = mutableSetOf<IntArray>()

        for (digit in variant.possibleDigits) {
            if (result == 0 || digit % result == 0) {
                val otherDigit: Int =
                    if (result == 0) {
                        0
                    } else {
                        digit / result
                    }
                if (digit != otherDigit && variant.possibleDigits.contains(otherDigit)) {
                    results += intArrayOf(digit, otherDigit)
                    results += intArrayOf(otherDigit, digit)
                }
            }
        }

        return results
    }
}
