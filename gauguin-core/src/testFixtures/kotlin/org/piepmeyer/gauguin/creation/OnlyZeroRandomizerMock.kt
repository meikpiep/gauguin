package org.piepmeyer.gauguin.creation

import org.piepmeyer.gauguin.Randomizer
import kotlin.random.Random

class OnlyZeroRandomizerMock : Randomizer {
    override fun random(): Random =
        object : Random() {
            override fun nextBits(bitCount: Int): Int = 0
        }

    override fun discard() {
        // nothing to do
    }

    override fun nextInt(maximumNumber: Int): Int = 0

    override fun nextDouble(): Double = 0.0
}
