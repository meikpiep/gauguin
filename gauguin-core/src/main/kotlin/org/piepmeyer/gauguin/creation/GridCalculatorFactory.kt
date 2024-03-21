package org.piepmeyer.gauguin.creation

import org.piepmeyer.gauguin.RandomSingleton
import org.piepmeyer.gauguin.Randomizer
import org.piepmeyer.gauguin.options.GameVariant

class GridCalculatorFactory {
    fun createCalculator(
        variant: GameVariant,
        randomizer: Randomizer = RandomSingleton.instance,
        shuffler: PossibleDigitsShuffler = RandomPossibleDigitsShuffler(),
    ): GridCalculator {
        return if (variant.gridSize.isSquare && !alwaysUseNewAlgorithm) {
            RandomCageGridCalculator(variant, randomizer, shuffler)
        } else {
            MergingCageGridCalculator(variant, randomizer, shuffler)
        }
    }

    companion object {
        var alwaysUseNewAlgorithm = false
    }
}
