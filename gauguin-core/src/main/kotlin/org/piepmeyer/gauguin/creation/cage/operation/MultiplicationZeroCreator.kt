package org.piepmeyer.gauguin.creation.cage.operation

import org.piepmeyer.gauguin.grid.GridCage

internal class MultiplicationZeroCreator(
    private val cage: GridCage,
    private val possibleDigits: Set<Int>,
    private val numberOfCells: Int,
) {
    private val numbers: IntArray = IntArray(numberOfCells)
    private var combinations = emptyList<IntArray>()

    fun create(): List<IntArray> {
        fillCombinations(false, numberOfCells)
        return combinations
    }

    private fun fillCombinations(
        zeroPresent: Boolean,
        numberOfCells: Int,
    ) {
        if (numberOfCells == 1 && !zeroPresent) {
            numbers[0] = 0
            if (cage.satisfiesConstraints(numbers)) {
                combinations += numbers.clone()
            }
            return
        }
        for (n in possibleDigits) {
            numbers[numberOfCells - 1] = n
            if (numberOfCells == 1) {
                if (cage.satisfiesConstraints(numbers)) {
                    combinations += numbers.clone()
                }
            } else {
                if (n == 0) {
                    fillCombinations(true, numberOfCells - 1)
                } else {
                    fillCombinations(zeroPresent, numberOfCells - 1)
                }
            }
        }
    }
}
