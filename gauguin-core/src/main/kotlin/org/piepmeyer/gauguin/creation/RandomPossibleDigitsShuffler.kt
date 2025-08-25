package org.piepmeyer.gauguin.creation

import org.piepmeyer.gauguin.RandomSingleton
import kotlin.random.Random

class RandomPossibleDigitsShuffler(
    private val random: Random = RandomSingleton.instance.getRandom(),
) : PossibleDigitsShuffler {
    override fun shufflePossibleDigits(possibleDigits: Set<Int>): List<Int> = possibleDigits.shuffled(random)
}
