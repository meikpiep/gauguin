package com.holokenmod.creation.cage

import com.holokenmod.grid.Grid

internal class MultiplicationCreator(
    private val cageCreator: GridSingleCageCreator,
    private val grid: Grid,
    private val targetSum: Int,
    private val numberOfCells: Int
) {
    fun create(): ArrayList<IntArray> {
        return if (targetSum == 0) {
            MultiplicationZeroCreator(
                cageCreator,
                grid,
                numberOfCells
            ).create()
        } else {
            MultiplicationNonZeroCreator(
                cageCreator,
                grid,
                targetSum,
                numberOfCells
            ).create()
        }
    }
}
