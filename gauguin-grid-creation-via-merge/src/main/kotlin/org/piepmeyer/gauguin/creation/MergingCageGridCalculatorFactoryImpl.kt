package org.piepmeyer.gauguin.creation

import org.piepmeyer.gauguin.Randomizer
import org.piepmeyer.gauguin.options.GameVariant

class MergingCageGridCalculatorFactoryImpl : MergingCageGridCalculatorFactory {
    override fun create(
        variant: GameVariant,
        randomizer: Randomizer,
        shuffler: PossibleDigitsShuffler,
    ): GridCalculator = MergingCageGridCalculator(variant, randomizer, shuffler)
}
