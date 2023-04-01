package com.holokenmod.creation.cage

import com.holokenmod.grid.Grid

internal class MultiplicationCreator(
    private val cageCreator: GridSingleCageCreator,
    private val grid: Grid,
    private val target_sum: Int,
    private val n_cells: Int
) {
    fun create(): ArrayList<IntArray> {
        return if (target_sum == 0) {
            MultiplicationZeroCreator(
                cageCreator,
                grid,
                n_cells
            ).create()
        } else MultiplicationNonZeroCreator(
            cageCreator,
            grid,
            target_sum,
            n_cells
        ).create()
    }
}