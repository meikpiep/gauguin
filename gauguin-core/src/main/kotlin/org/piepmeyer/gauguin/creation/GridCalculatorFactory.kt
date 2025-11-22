package org.piepmeyer.gauguin.creation

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.RandomSingleton
import org.piepmeyer.gauguin.Randomizer
import org.piepmeyer.gauguin.options.GameVariant

class GridCalculatorFactory : KoinComponent {
    private val mergingCageGridCalculatorFactory: MergingCageGridCalculatorFactory by inject()

    fun createCalculator(
        variant: GameVariant,
        randomizer: Randomizer = RandomSingleton.instance,
        shuffler: PossibleDigitsShuffler = RandomPossibleDigitsShuffler(),
    ): GridCalculator =
        if (variant.gridSize.isSquare && !alwaysUseNewAlgorithm) {
            RandomCageGridCalculator(variant, randomizer, shuffler)
        } else {
            mergingCageGridCalculatorFactory.create(variant, randomizer, shuffler)
        }

    companion object {
        var alwaysUseNewAlgorithm = false
    }
}
