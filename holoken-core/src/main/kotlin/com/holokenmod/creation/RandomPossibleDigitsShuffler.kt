package com.holokenmod.creation

import com.holokenmod.RandomSingleton

class RandomPossibleDigitsShuffler : PossibleDigitsShuffler {
    override fun shufflePossibleDigits(possibleDigits: Set<Int>): List<Int> {
        return possibleDigits.shuffled(RandomSingleton.instance.getRandom())
    }
}
