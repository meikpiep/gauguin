package org.piepmeyer.gauguin.creation.cage.operation

import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.options.GameVariant

class AdditionCreator(
    private val cage: GridCage,
    private val variant: GameVariant,
    private val targetSum: Int,
    private val numberOfCells: Int,
) {
    private val numbers = IntArray(numberOfCells)
    private val possibleCombinations = mutableSetOf<IntArray>()

    fun create(): Set<IntArray> {
        getaddcombos(targetSum, numberOfCells)

        return possibleCombinations
    }

    private fun getaddcombos(
        targetSum: Int,
        numberOfCells: Int,
    ) {
        if (numberOfCells == 1) {
            if (variant.possibleDigits.contains(targetSum)) {
                numbers[0] = targetSum
                if (cage.satisfiesConstraints(numbers)) {
                    possibleCombinations.add(numbers.clone())
                }
            }
            return
        }
        for (n in variant.possibleDigits) {
            numbers[numberOfCells - 1] = n
            getaddcombos(targetSum - n, numberOfCells - 1)
        }
    }
}
