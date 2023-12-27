package org.piepmeyer.gauguin.creation

import org.piepmeyer.gauguin.Randomizer
import kotlin.random.Random

class SeedRandomizerMock(
    seed: Int,
) : Randomizer {
    val random = Random(seed)

    override fun discard() {
        // nothing to do
    }

    override fun nextInt(maximumNumber: Int): Int {
        return random.nextInt(maximumNumber)
    }

    override fun nextDouble(): Double {
        return random.nextDouble()
    }
}
