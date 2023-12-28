package org.piepmeyer.gauguin

import kotlin.random.Random

interface Randomizer {
    fun random(): Random

    fun discard()

    fun nextInt(maximumNumber: Int): Int

    fun nextDouble(): Double
}
