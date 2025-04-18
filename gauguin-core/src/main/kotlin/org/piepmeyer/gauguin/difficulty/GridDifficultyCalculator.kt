package org.piepmeyer.gauguin.difficulty

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.grid.Grid
import java.math.BigInteger
import kotlin.math.ln

private val logger = KotlinLogging.logger {}

class GridDifficultyCalculator(
    private val grid: Grid,
) {
    fun ensureDifficultyCalculated() {
        if (grid.difficulty.classicalRating != null) {
            return
        }

        grid.difficulty =
            grid.difficulty.copy(
                classicalRating = calculate(),
            )
    }

    fun calculate(): Double {
        logger.debug { "Calculating difficulty of variant ${grid.variant}" }

        val difficulty =
            grid.cages
                .map { cage ->
                    val cageCreator = GridSingleCageCreator(grid.variant, cage)

                    BigInteger.valueOf(cageCreator.possibleCombinations.size.toLong())
                }.reduce { acc: BigInteger, bigInteger: BigInteger ->
                    acc.multiply(bigInteger)
                }

        val value = ln(difficulty.toDouble())

        logger.debug { "difficulty: $value" }

        return value
    }
}
