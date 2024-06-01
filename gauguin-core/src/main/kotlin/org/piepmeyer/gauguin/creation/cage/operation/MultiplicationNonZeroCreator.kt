package org.piepmeyer.gauguin.creation.cage.operation

import org.piepmeyer.gauguin.grid.GridCage

class MultiplicationNonZeroCreator(
    private val cage: GridCage,
    private val possibleNonZeroDigits: Set<Int>,
    private val targetValue: Int,
    private val numberOfCells: Int,
) {
    private var numbers: IntArray = IntArray(numberOfCells)
    private var combinations = emptyList<IntArray>()

    fun create(): List<IntArray> {
        fillCombinations(targetValue, numberOfCells)
        return combinations
    }

    private fun fillCombinations(
        targetValue: Int,
        numberOfCells: Int,
    ) {
        if (numberOfCells == 1) {
            if (targetValue in possibleNonZeroDigits) {
                numbers[0] = targetValue
                if (cage.satisfiesConstraints(numbers)) {
                    combinations += numbers.clone()
                }
            }

            return
        }

        possibleNonZeroDigits
            .filter { targetValue % it == 0 }
            .forEach {
                numbers[numberOfCells - 1] = it
                fillCombinations(targetValue / it, numberOfCells - 1)
            }
    }
}
