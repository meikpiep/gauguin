package org.piepmeyer.gauguin.creation

import org.piepmeyer.gauguin.RandomSingleton
import org.piepmeyer.gauguin.Randomizer
import org.piepmeyer.gauguin.options.GameVariant

interface MergingCageGridCalculatorFactory {
    fun create(
        variant: GameVariant,
        randomizer: Randomizer = RandomSingleton.Companion.instance,
        shuffler: PossibleDigitsShuffler = RandomPossibleDigitsShuffler(),
    ): GridCalculator
}
