package com.holokenmod.creation.cage

import com.holokenmod.grid.Grid

class AdditionCreator(
    private val creator: GridSingleCageCreator,
    private val grid: Grid,
    private val targetSum: Int,
    private val numberOfCells: Int
) {

    private val numbers = IntArray(numberOfCells)
    private val possibleCombinations = ArrayList<IntArray>()

    fun create(): ArrayList<IntArray> {
        getaddcombos(targetSum, numberOfCells)

        return possibleCombinations
    }

    private fun getaddcombos(targetSum: Int, numberOfCells: Int) {
        if (numberOfCells == 1) {
            if (grid.possibleDigits.contains(targetSum)) {
                numbers[0] = targetSum
                if (creator.satisfiesConstraints(numbers)) {
                    possibleCombinations.add(numbers.clone())
                }
            }
            return
        }
        for (n in grid.possibleDigits) {
            numbers[numberOfCells - 1] = n
            getaddcombos(targetSum - n, numberOfCells - 1)
        }
    }
}
