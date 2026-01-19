package org.piepmeyer.gauguin.creation.dlx

import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class UniqueIndexSetsOfGivenLength(
    private val maximumValue: Int,
    private val numberOfCopies: Int,
) {
    fun calculateProduct(): Set<IntArray> {
        if (numberOfCopies == 1) {
            return (0..maximumValue).map { intArrayOf(it) }.toSet()
        }

        val result = mutableSetOf<IntArray>()

        val indexOfCopy =
            IntArray(
                numberOfCopies,
            ) { it } // initialize with values 0, 1, 2,...

        var currentCopy = numberOfCopies - 1

        while (indexOfCopy[0] <= maximumValue) {
            var isOrdered = true

            for (i in 1..<indexOfCopy.size) {
                if (indexOfCopy[i] < indexOfCopy[i - 1]) {
                    isOrdered = false
                    break
                }
            }

            if (isOrdered) {
                result += indexOfCopy.copyOf()
            }

            indexOfCopy[currentCopy] = incrementIndexToUniqueValue(indexOfCopy, currentCopy)

            if (indexOfCopy[currentCopy] == maximumValue + 1) {
                do {
                    currentCopy--
                    indexOfCopy[currentCopy] = incrementIndexToUniqueValue(indexOfCopy, currentCopy)
                } while (currentCopy > 0 && indexOfCopy[currentCopy] == maximumValue + 1)

                for (i in currentCopy + 1 until numberOfCopies) {
                    indexOfCopy[i] = -1
                    indexOfCopy[i] = incrementIndexToUniqueValue(indexOfCopy, i)
                }
                currentCopy = numberOfCopies - 1
            }
        }

        logger.debug { "result is of size ${result.size}" }

        return result
    }

    private fun incrementIndexToUniqueValue(
        indexOfCopy: IntArray,
        currentCopy: Int,
    ): Int {
        var newIndexValue = indexOfCopy[currentCopy] + 1

        while ((0 until currentCopy).contains(indexOfCopy.indexOfFirst { it == newIndexValue })) {
            newIndexValue++
        }
        return newIndexValue
    }
}
