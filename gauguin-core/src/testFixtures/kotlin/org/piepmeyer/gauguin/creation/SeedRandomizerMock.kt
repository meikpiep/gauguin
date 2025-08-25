package org.piepmeyer.gauguin.creation

import org.piepmeyer.gauguin.Randomizer
import kotlin.random.Random

class SeedRandomizerMock(
    seed: Int,
) : Randomizer {
    val random = Random(seed)

    override fun random(): Random = random

    override fun discard() {
        // nothing to do
    }

    override fun nextInt(maximumNumber: Int): Int = random.nextInt(maximumNumber)

    override fun nextDouble(): Double = random.nextDouble()
}
