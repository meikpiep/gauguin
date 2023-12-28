package org.piepmeyer.gauguin.creation

import org.piepmeyer.gauguin.Randomizer
import kotlin.random.Random

class OnlyZeroRandomizerMock : Randomizer {
    override fun random(): Random {
        return object : Random() {
            override fun nextBits(bitCount: Int): Int {
                return 0
            }
        }
    }

    override fun discard() {
        // nothing to do
    }

    override fun nextInt(maximumNumber: Int): Int {
        return 0
    }

    override fun nextDouble(): Double {
        return 0.0
    }
}
