package org.piepmeyer.gauguin.creation.cage.operation

import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.options.GameVariant

internal class MultiplicationCreator(
    private val cage: GridCage,
    private val variant: GameVariant,
    private val targetSum: Int,
    private val numberOfCells: Int,
) {
    fun create(): Set<IntArray> =
        if (targetSum == 0) {
            MultiplicationZeroCreator(
                cage,
                variant.possibleDigits,
                numberOfCells,
            ).create()
        } else {
            MultiplicationNonZeroCreator(
                cage,
                variant.possibleNonZeroDigits,
                targetSum,
                numberOfCells,
            ).create()
        }
}
