package org.piepmeyer.gauguin.creation

fun interface PossibleDigitsShuffler {
    fun shufflePossibleDigits(possibleDigits: Set<Int>): List<Int>
}
