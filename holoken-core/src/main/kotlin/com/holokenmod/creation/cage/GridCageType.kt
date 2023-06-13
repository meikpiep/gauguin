package com.holokenmod.creation.cage

enum class GridCageType(
    val coordinates: Array<Pair<Int, Int>>,
    val satisfiesConstraints: (IntArray) -> Boolean
) {
    SINGLE(arrayOf(Pair(0, 0)),
        { true }),
    DOUBLE_HORIZONTAL(arrayOf(Pair(0, 0), Pair(0, 1)),
        { it[0] != it[1] }),
    DOUBLE_VERTICAL(arrayOf(Pair(0, 0), Pair(1, 0)),
        { it[0] != it[1] }),
    TRIPLE_HORIZONTAL(arrayOf(Pair(0, 0), Pair(0, 1), Pair(0, 2)),
        { it[0] != it[1] && it[0] != it[2] && it[1] != it[2] }),
    TRIPLE_VERTICAL(arrayOf(Pair(0, 0), Pair(1, 0), Pair(2, 0)),
        { it[0] != it[1] && it[0] != it[2] && it[1] != it[2] }),
    ANGLE_ONE(arrayOf(Pair(0, 0), Pair(0, 1), Pair(1, 1)),
        { it[0] != it[1] && it[1] != it[2] }),
    ANGLE_TWO(arrayOf(Pair(0, 0), Pair(0, 1), Pair(-1, 1)),
        { it[0] != it[1] && it[1] != it[2] }),
    ANGLE_THREE(arrayOf(Pair(0, 0), Pair(1, 0), Pair(1, 1)),
        { it[0] != it[1] && it[1] != it[2] }),
    ANGLE_FOUR(arrayOf(Pair(0, 0), Pair(1, 0), Pair(0, 1)),
        { it[0] != it[1] && it[1] != it[2] }),
    SQUARE(arrayOf(Pair(0, 0), Pair(1, 0), Pair(0, 1), Pair(1, 1)),
        { it[0] != it[1] && it[0] != it[2] && it[1] != it[3] && it[2] != it[3] }),
    L_VERTICAL_SHORT_RIGHT_TOP(arrayOf(Pair(0, 0), Pair(1, 0), Pair(0, 1), Pair(0, 2)),
        { it[0] != it[1] && it[0] != it[2] && it[0] != it[3] && it[2] != it[3] }),
    L_HORIZONTAL_SHORT_RIGHT_BOTTOM(arrayOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(2, 1)),
        { it[0] != it[1] && it[0] != it[2] && it[1] != it[2] && it[2] != it[3] }),
    L_VERTICAL_SHORT_LEFT_BOTTOM(arrayOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(-1, 2)),
        { it[0] != it[1] && it[0] != it[2] && it[1] != it[2] && it[2] != it[3] }),
    L_HORIZONTAL_SHORT_LEFT_BOTTOM(arrayOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(0, 1)),
        { it[0] != it[1] && it[0] != it[2] && it[0] != it[3] && it[1] != it[2] });
}
