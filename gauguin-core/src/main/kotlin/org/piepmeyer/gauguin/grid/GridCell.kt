package org.piepmeyer.gauguin.grid

class GridCell(
    val cellNumber: Int,
    val row: Int,
    val column: Int,
    var value: Int = NO_VALUE_SET,
    var userValue: Int = NO_VALUE_SET,
) {
    var cage: GridCage? = null
    var isCheated = false
    var possibles: Set<Int> = setOf()
    var duplicatedInRowOrColumn = false
    var isSelected = false
    var isLastModified = false
    var isInvalidHighlight = false

    fun cage(): GridCage = cage!!

    val isUserValueCorrect: Boolean
        get() = userValue == value
    val isUserValueSet: Boolean
        get() = userValue != NO_VALUE_SET

    fun cellInAnyCage(): Boolean {
        return cage != null
    }

    fun setUserValueExtern(value: Int) {
        clearPossibles()
        userValue = value
        isInvalidHighlight = false
    }

    fun setUserValueIntern(value: Int) {
        userValue = value
    }

    fun clearUserValue() {
        userValue = NO_VALUE_SET
    }

    fun togglePossible(digit: Int) {
        possibles =
            if (!isPossible(digit)) {
                possibles + digit
            } else {
                possibles - digit
            }
    }

    fun isPossible(digit: Int): Boolean {
        return possibles.contains(digit)
    }

    fun removePossible(digit: Int) {
        possibles = possibles - digit
    }

    fun clearPossibles() {
        possibles = setOf()
    }

    fun addPossible(digit: Int) {
        possibles = possibles + digit
    }

    fun shouldBeHighlightedInvalid(): Boolean {
        return isUserValueSet && !isUserValueCorrect
    }

    fun possiblesToBeFilled(): Set<Int> {
        if (isUserValueSet) {
            return emptySet()
        }

        val otherCageCells = cage().cells - this

        val setsOfPossibles =
            otherCageCells.filter { it.possibles.isNotEmpty() }
                .map { it.possibles }
                .toSet()

        if (setsOfPossibles.size == 1) {
            return setsOfPossibles.first()
        }

        if (setsOfPossibles.size == 2) {
            val first = setsOfPossibles.first()
            val second = setsOfPossibles.elementAt(1)

            if (first.containsAll(second)) {
                return second
            }

            if (second.containsAll(first)) {
                return first
            }
        }

        return emptySet()
    }

    companion object {
        const val NO_VALUE_SET = Int.MAX_VALUE
    }
}
