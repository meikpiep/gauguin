package org.piepmeyer.gauguin.grid

class GridCell(
    val cellNumber: Int,
    val row: Int,
    val column: Int
) {
    var value = NO_VALUE_SET
    var userValue = NO_VALUE_SET

    var cage: GridCage? = null
    var isCheated = false
    var possibles: Set<Int> = setOf()
    var duplicatedInRowOrColumn = false
    var isSelected = false
    var isLastModified = false
    var isInvalidHighlight = false

    fun cage(): GridCage = cage!!

    override fun toString(): String {
        return "GridCell{" +
            "mColumn=" + column +
            ", mRow=" + row +
            ", mValue=" + value +
            '}'
    }

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
        possibles = if (!isPossible(digit)) {
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

    fun addPossibles(digits: Set<Int>) {
        possibles = digits
    }

    companion object {
        const val NO_VALUE_SET = Int.MAX_VALUE
    }
}
