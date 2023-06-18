package com.holokenmod.creation.cage

import com.holokenmod.options.GameVariant

class MultiplicationNonZeroCreator(
    private val cageCreator: GridSingleCageCreator,
    private val variant: GameVariant,
    private val targetValue: Int,
    private val numberOfCells: Int
) {
    private var numbers: IntArray = IntArray(numberOfCells)
    private val combinations = ArrayList<IntArray>()

    fun create(): ArrayList<IntArray> {
        fillCombinations(targetValue, numberOfCells)
        return combinations
    }

    private fun fillCombinations(targetValue: Int, numberOfCells: Int) {
        for (n in variant.possibleNonZeroDigits) {
            if (targetValue % n != 0) {
                continue
            }
            if (numberOfCells == 1) {
                if (n == targetValue) {
                    numbers[0] = n
                    if (cageCreator.satisfiesConstraints(numbers)) {
                        combinations.add(numbers.clone())
                    }
                }
            } else {
                numbers[numberOfCells - 1] = n
                fillCombinations(targetValue / n, numberOfCells - 1)
            }
        }
    }
}
