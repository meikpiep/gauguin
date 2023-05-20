package com.holokenmod.creation

import com.holokenmod.creation.cage.GridSingleCageCreator
import com.holokenmod.grid.Grid
import com.holokenmod.options.DigitSetting
import com.holokenmod.options.GameDifficulty
import com.holokenmod.options.GridCageOperation
import com.holokenmod.options.SingleCageUsage
import java.math.BigInteger
import kotlin.math.ln
import kotlin.math.roundToLong

class GridDifficultyCalculator(private val grid: Grid) {
    fun calculate(): Double {
        var difficulty = BigInteger.ONE
        for (cage in grid.cages) {
            val cageCreator = GridSingleCageCreator(grid, cage)
            difficulty =
                difficulty.multiply(BigInteger.valueOf(cageCreator.possibleNums.size.toLong()))
        }
        val value = ln(difficulty.toDouble())
        println("difficulty: $value")
        return value
    }

    val info: String
        get() {
            val difficultyValue = calculate().roundToLong().toDouble()
            val difficultyAsText = difficultyValue.roundToLong().toString()
            return if (!isGridVariantSupported) {
                difficultyAsText
            } else {
                difficultyAsText
            }
        }
    val isGridVariantSupported: Boolean
        get() = grid.options.digitSetting == DigitSetting.FIRST_DIGIT_ONE &&
            grid.options.showOperators &&
            grid.options.singleCageUsage == SingleCageUsage.FIXED_NUMBER &&
            grid.options.cageOperation == GridCageOperation.OPERATIONS_ALL &&
            grid.gridSize.height == 9 && grid.gridSize.width == 9

    val difficulty: GameDifficulty
        get() = getDifficulty(calculate())

    private fun getDifficulty(difficultyValue: Double): GameDifficulty {
        if (difficultyValue >= 86.51) {
            return GameDifficulty.EXTREME
        }
        if (difficultyValue >= 80.80) {
            return GameDifficulty.HARD
        }
        if (difficultyValue >= 76.20) {
            return GameDifficulty.MEDIUM
        }
        return if (difficultyValue >= 70.40) {
            GameDifficulty.EASY
        } else {
            GameDifficulty.VERY_EASY
        }
    }
}
