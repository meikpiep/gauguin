package org.piepmeyer.gauguin.creation

import org.piepmeyer.gauguin.Randomizer

class OnlyZeroRandomizerMock : Randomizer {
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
