package com.holokenmod.creation.dlx

class UniqueIndexSetsOfGivenLength(
    private val values: List<Int>,
    private val numberOfCopies: Int
) {
    fun calculateProduct(): Set<Set<Int>> {
        if (numberOfCopies == 1) {
            return values.map { setOf(it) }.toSet()
        }

        val result = mutableSetOf<Set<Int>>()

        val indexOfCopy = IntArray(
            numberOfCopies
        ) { it } // initialize with values 0, 1, 2,...

        /*val indexOfCopy = IntArray(numberOfCopies - 1
        ) { it } //initialize with values 0, 1, 2,...

        val lowestPossibleIndices = values.subList(indexOfCopy.max(), values.size)

        for (lowestPossible in lowestPossibleIndices) {
            val resultItem = mutableSetOf<Int>()

            for (i in indexOfCopy) {
                resultItem += values[indexOfCopy[i]]
            }

            result += resultItem
        }*/

        var currentCopy = numberOfCopies - 1

        while (indexOfCopy[0] < values.size) {
            val resultItem = mutableSetOf<Int>()

            for (i in 0 until numberOfCopies) {
                resultItem += values[indexOfCopy[i]]
            }

            result += resultItem

            indexOfCopy[currentCopy] = incrementIndexToUniqueValue(indexOfCopy, currentCopy)

            if (indexOfCopy[currentCopy] == values.size) {
                do {
                    currentCopy--
                    indexOfCopy[currentCopy] = incrementIndexToUniqueValue(indexOfCopy, currentCopy)
                } while (currentCopy > 0 && indexOfCopy[currentCopy] == values.size)

                for (i in currentCopy + 1 until numberOfCopies) {
                    indexOfCopy[i] = -1
                    indexOfCopy[i] = incrementIndexToUniqueValue(indexOfCopy, i)
                }
                currentCopy = numberOfCopies - 1
            }
        }

        print("result: $result")

        return result
    }

    private fun incrementIndexToUniqueValue(indexOfCopy: IntArray, currentCopy: Int): Int {
        var newIndexValue = indexOfCopy[currentCopy] + 1

        while ((0 until currentCopy).contains(indexOfCopy.indexOfFirst { it == newIndexValue })) {
            newIndexValue++
        }
        return newIndexValue
    }
}
