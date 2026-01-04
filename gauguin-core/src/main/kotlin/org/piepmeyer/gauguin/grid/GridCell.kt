package org.piepmeyer.gauguin.grid

class GridCell(
    val cellNumber: Int,
    val row: Int,
    val column: Int,
    var value: Int = NO_VALUE_SET,
    var userValue: Int? = null,
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
        get() = userValue != null

    fun cellInAnyCage(): Boolean = cage != null

    fun setUserValueExtern(value: Int?) {
        clearPossibles()
        userValue = value
        isInvalidHighlight = false
    }

    fun setUserValueIntern(value: Int?) {
        userValue = value
    }

    fun clearUserValue() {
        userValue = null
    }

    fun togglePossible(digit: Int?) {
        if (digit == null) {
            return
        }

        possibles =
            if (!isPossible(digit)) {
                possibles + digit
            } else {
                possibles - digit
            }
    }

    fun isPossible(digit: Int?): Boolean = digit != null && possibles.contains(digit)

    fun removePossible(digit: Int?) {
        if (digit == null) {
            return
        }

        possibles = possibles - digit
    }

    fun clearPossibles() {
        possibles = setOf()
    }

    fun addPossible(digit: Int) {
        possibles = possibles + digit
    }

    fun shouldBeHighlightedInvalid(): Boolean = isUserValueSet && !isUserValueCorrect

    fun displayableUserValueOrPossibles() =
        if (isUserValueSet) {
            userValue.toString()
        } else {
            possibles.map { it.toString() }.toString()
        }

    override fun toString(): String = "GridCell cellNumber=$cellNumber"

    companion object {
        const val NO_VALUE_SET = Int.MAX_VALUE
    }
}
