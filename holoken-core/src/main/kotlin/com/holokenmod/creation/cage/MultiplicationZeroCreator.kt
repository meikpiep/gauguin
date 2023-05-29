package com.holokenmod.creation.cage

import com.holokenmod.options.GameVariant

internal class MultiplicationZeroCreator(
    private val cageCreator: GridSingleCageCreator,
    private val variant: GameVariant,
    private val numberOfCells: Int
) {
    private val numbers: IntArray = IntArray(numberOfCells)
    private val combinations = ArrayList<IntArray>()

    fun create(): ArrayList<IntArray> {
        fillCombinations(false, numberOfCells)
        return combinations
    }

    private fun fillCombinations(zeroPresent: Boolean, numberOfCells: Int) {
        if (numberOfCells == 1 && !zeroPresent) {
            numbers[0] = 0
            if (cageCreator.satisfiesConstraints(numbers)) {
                combinations.add(numbers.clone())
            }
            return
        }
        for (n in variant.possibleDigits) {
            numbers[numberOfCells - 1] = n
            if (numberOfCells == 1) {
                if (cageCreator.satisfiesConstraints(numbers)) {
                    combinations.add(numbers.clone())
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
