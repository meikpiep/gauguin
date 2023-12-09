package org.piepmeyer.gauguin.creation.cage

import org.piepmeyer.gauguin.options.GameVariant

internal class MultiplicationCreator(
    private val cageCreator: GridSingleCageCreator,
    private val variant: GameVariant,
    private val targetSum: Int,
    private val numberOfCells: Int,
) {
    fun create(): ArrayList<IntArray> {
        return if (targetSum == 0) {
            MultiplicationZeroCreator(
                cageCreator,
                variant,
                numberOfCells,
            ).create()
        } else {
            MultiplicationNonZeroCreator(
                cageCreator,
                variant,
                targetSum,
                numberOfCells,
            ).create()
        }
    }
}
