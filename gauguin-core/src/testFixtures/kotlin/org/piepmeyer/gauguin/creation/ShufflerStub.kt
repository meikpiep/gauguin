package org.piepmeyer.gauguin.creation

class ShufflerStub : PossibleDigitsShuffler {
    override fun shufflePossibleDigits(possibleDigits: Set<Int>): List<Int> = possibleDigits.toList()
}
