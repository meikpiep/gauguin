package org.piepmeyer.gauguin.difficulty

import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.grid.Grid
import java.math.BigInteger

class AlternativeGridDifficultyCalculator(
    private val grid: Grid,
) {
    fun calculate(): Double {
        val possibleCombinationsByAddition = getPossibleCombinations()

        val listSum =
            possibleCombinationsByAddition
                .reduce { acc: BigInteger, bigInteger: BigInteger ->
                    acc.plus(bigInteger)
                }

        return (listSum.toDouble() / grid.variant.surfaceArea.toDouble())
    }

    private fun getPossibleCombinations(): List<BigInteger> =
        grid.cages
            .map { cage ->
                val cageCreator = GridSingleCageCreator(grid.variant, cage)

                cage.cells.mapIndexed { cellIndex, _ ->
                    cageCreator.possibleCombinations
                        .map { it[cellIndex] }
                        .distinct()
                        .size
                        .toBigInteger()
                }
            }.flatten()
}
