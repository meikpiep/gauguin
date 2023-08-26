package com.holokenmod.creation

import com.holokenmod.RandomSingleton
import com.holokenmod.Randomizer
import com.holokenmod.creation.dlx.DLX
import com.holokenmod.creation.dlx.MathDokuDLX
import com.holokenmod.grid.Grid
import com.holokenmod.options.GameVariant
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class GridCalculator(
    private val randomizer: Randomizer,
    private val shuffler: PossibleDigitsShuffler,
    private val variant: GameVariant
) {
    constructor(variant: GameVariant) : this(
        RandomSingleton.instance,
        RandomPossibleDigitsShuffler(),
        variant
    )

    fun calculate(): Grid {
        var dlxNumber: Int
        var numAttempts = 0
        var sumDLXDuration: Long = 0

        var grid: Grid

        do {
            grid = GridCreator(variant, randomizer, shuffler).createRandomizedGridWithCages()
            numAttempts++
            val dlxMillis = System.currentTimeMillis()
            val mdd = MathDokuDLX(grid)
            // Stop solving as soon as we find multiple solutions
            dlxNumber = mdd.solve(DLX.SolveType.MULTIPLE)
            val dlxDuration = System.currentTimeMillis() - dlxMillis
            sumDLXDuration += dlxDuration
            logger.info { "DLX Num Solns = $dlxNumber in $dlxDuration ms" }

            if (dlxNumber == 0) {
                logger.debug { grid.toString() }
            }
        } while (dlxNumber != 1)

        val averageDLX = sumDLXDuration / numAttempts
        logger.debug { "DLX Num Attempts = $numAttempts in $sumDLXDuration ms (average $averageDLX ms)" }

        grid.clearUserValues()

        return grid
    }
}
