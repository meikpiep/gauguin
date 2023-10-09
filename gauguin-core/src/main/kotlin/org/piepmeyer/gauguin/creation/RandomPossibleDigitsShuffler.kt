package org.piepmeyer.gauguin.creation

import org.piepmeyer.gauguin.RandomSingleton

class RandomPossibleDigitsShuffler : PossibleDigitsShuffler {
    override fun shufflePossibleDigits(possibleDigits: Set<Int>): List<Int> {
        return possibleDigits.shuffled(RandomSingleton.instance.getRandom())
    }
}
