package org.piepmeyer.gauguin.difficulty

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.grid.Grid
import java.math.BigInteger

private val logger = KotlinLogging.logger {}

class AlternativeGridDifficultyCalculator(
    private val grid: Grid,
) {
    fun calculate(): Double {
        logger.debug { "Calculating alternative difficulty of variant ${grid.variant}" }

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
