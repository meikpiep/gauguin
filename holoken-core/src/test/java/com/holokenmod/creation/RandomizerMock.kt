package com.holokenmod.creation

import com.holokenmod.Randomizer

class RandomizerMock : Randomizer {
    override fun discard() {
        //nothing to do
    }

    override fun nextInt(maximumNumber: Int): Int {
        return 0
    }

    override fun nextDouble(): Double {
        return 0.0
    }
}