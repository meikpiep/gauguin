package org.piepmeyer.gauguin

import kotlin.random.Random

class RandomSingleton : Randomizer {
    private var random = Random.Default

    override fun random(): Random = random

    override fun discard() {
        random = Random.Default
    }

    override fun nextInt(maximumNumber: Int): Int = random.nextInt(maximumNumber)

    override fun nextDouble(): Double = random.nextDouble()

    fun getRandom(): Random = random

    companion object {
        val instance = RandomSingleton()
    }
}
