package org.piepmeyer.gauguin.creation

import io.kotest.core.spec.style.FunSpec
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.SingleCageUsage

class TestGridCalculationPerformance : FunSpec({
    xtest("10x10").config(invocations = 1) {
        val variant = GameVariant(
            GridSize(3, 3),
            GameOptionsVariant.createClassic().copy(singleCageUsage = SingleCageUsage.FIXED_NUMBER)
        )

        val gridOne = calculateGrid(variant)

        println(gridOne)
    }
})

private suspend fun calculateGrid(variant: GameVariant): Grid {
    val randomizer = SeedRandomizerMock(1)

    val creator = GridCalculator(
        variant,
        randomizer,
        RandomPossibleDigitsShuffler(randomizer.random)
    )

    return creator.calculate()
}
