package com.holokenmod

import java.util.*

class RandomSingleton : Randomizer {
    private var random = Random()

    override fun discard() {
        random = Random()
    }

    override fun nextInt(maximumNumber: Int): Int {
        return random.nextInt(maximumNumber)
    }

    override fun nextDouble(): Double {
        return random.nextDouble()
    }

    fun getRandom(): Random {
        return random
    }

    companion object {
        val instance = RandomSingleton()
    }
}
