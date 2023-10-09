package org.piepmeyer.gauguin.creation

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.creation.cage.GridSingleCageCreator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.GameDifficulty
import org.piepmeyer.gauguin.options.GameDifficultyRatings
import org.piepmeyer.gauguin.options.GameVariant
import java.math.BigInteger
import kotlin.math.ln
import kotlin.math.roundToLong

private val logger = KotlinLogging.logger {}

class GridDifficultyCalculator(private val grid: Grid) {
    fun calculate(): Double {
        val difficulty = grid.cages
            .map { cage ->
                val cageCreator = GridSingleCageCreator(grid.variant, cage)

                BigInteger.valueOf(cageCreator.possibleNums.size.toLong())
            }
            .reduce { acc: BigInteger, bigInteger: BigInteger ->
                acc.multiply(bigInteger)
            }

        val value = ln(difficulty.toDouble())

        logger.debug { "difficulty: $value" }

        return value
    }

    fun info(): String {
        val difficultyValue = calculate().roundToLong().toDouble()
        return difficultyValue.roundToLong().toString()
    }

    val isGridVariantSupported: Boolean
        get() = isSupported(grid.variant)

    val difficulty: GameDifficulty
        get() = getDifficulty(calculate())

    private fun getDifficulty(difficultyValue: Double): GameDifficulty {
        val ratings = GameDifficultyRatings.byVariant(grid.variant)
            ?: return GameDifficulty.VERY_EASY

        return ratings.getDifficulty(difficultyValue)
    }

    companion object {
        fun isSupported(variant: GameVariant): Boolean {
            return GameDifficultyRatings.isSupported(variant)
        }
    }
}
