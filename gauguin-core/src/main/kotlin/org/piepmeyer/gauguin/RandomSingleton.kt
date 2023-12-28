package org.piepmeyer.gauguin

import kotlin.random.Random

class RandomSingleton : Randomizer {
    private var random = Random.Default

    override fun random(): Random {
        return random
    }

    override fun discard() {
        random = Random.Default
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
