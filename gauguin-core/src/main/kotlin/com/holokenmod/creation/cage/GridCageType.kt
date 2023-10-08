package com.holokenmod.creation.cage

enum class GridCageType(
    val coordinates: Array<Pair<Int, Int>>,
    val satisfiesConstraints: (IntArray) -> Boolean,
    val borderInfos: List<BorderInfo>
) {
    SINGLE(
        arrayOf(Pair(0, 0)),
        { true },
        BorderInfo.rectangle(1, 1)
    ),
    DOUBLE_HORIZONTAL(
        arrayOf(Pair(0, 0), Pair(1, 0)),
        { it[0] != it[1] },
        BorderInfo.rectangle(2, 1)
    ),
    DOUBLE_VERTICAL(
        arrayOf(Pair(0, 0), Pair(0, 1)),
        { it[0] != it[1] },
        BorderInfo.rectangle(1, 2)
    ),
    TRIPLE_HORIZONTAL(
        arrayOf(Pair(0, 0), Pair(1, 0), Pair(2, 0)),
        { it[0] != it[1] && it[0] != it[2] && it[1] != it[2] },
        BorderInfo.rectangle(3, 1)
    ),
    TRIPLE_VERTICAL(
        arrayOf(Pair(0, 0), Pair(0, 1), Pair(0, 2)),
        { it[0] != it[1] && it[0] != it[2] && it[1] != it[2] },
        BorderInfo.rectangle(1, 3)
    ),
    ANGLE_RIGHT_BOTTOM(
        arrayOf(Pair(0, 0), Pair(1, 0), Pair(0, 1)),
        { it[0] != it[1] && it[0] != it[2] },
        BorderInfoBuilder().down(2, 2).right(1, 2)
            .up().right()
            .up(1, 2).left(2, 2).build()
    ),
    ANGLE_LEFT_TOP(
        arrayOf(Pair(0, 0), Pair(0, 1), Pair(-1, 1)),
        { it[0] != it[1] && it[1] != it[2] },
        BorderInfoBuilder().down(1).left()
            .down(1, 2).right(2, 2)
            .up(2, 2).left(1, 2).build()
    ),
    ANGLE_LEFT_BOTTOM(
        arrayOf(Pair(0, 0), Pair(1, 0), Pair(1, 1)),
        { it[0] != it[1] && it[1] != it[2] },
        BorderInfoBuilder().down(1, 2).right()
            .down().right(1, 2)
            .up(2, 2).left(2, 2).build()
    ),
    ANGLE_RIGHT_TOP(
        arrayOf(Pair(0, 0), Pair(0, 1), Pair(1, 1)),
        { it[0] != it[1] && it[1] != it[2] },
        BorderInfoBuilder().down(2, 2).right(2, 2)
            .up(1, 2).left()
            .up().left(1, 2).build()
    ),
    SQUARE(
        arrayOf(Pair(0, 0), Pair(1, 0), Pair(0, 1), Pair(1, 1)),
        { it[0] != it[1] && it[0] != it[2] && it[1] != it[3] && it[2] != it[3] },
        BorderInfo.rectangle(2, 2)
    ),

    /* 0 3
     * 1
     * 2
     */
    L_VERTICAL_SHORT_RIGHT_TOP(
        arrayOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(1, 0)),
        { it[0] != it[1] && it[0] != it[2] && it[0] != it[3] && it[1] != it[2] },
        BorderInfoBuilder().down(3, 2).right(1, 2)
            .up(2).right()
            .up(1, 2).left(2, 2).build()
    ),

    /* 0 1 2
     *     3
     */
    L_HORIZONTAL_SHORT_RIGHT_BOTTOM(
        arrayOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(2, 1)),
        { it[0] != it[1] && it[0] != it[2] && it[1] != it[2] && it[2] != it[3] },
        BorderInfoBuilder().down(1, 2).right(2)
            .down().right(1, 2)
            .up(2, 2).left(3, 2).build()
    ),

    /*   0
         1
     * 3 2
     */
    L_VERTICAL_SHORT_LEFT_BOTTOM(
        arrayOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(-1, 2)),
        { it[0] != it[1] && it[0] != it[2] && it[1] != it[2] && it[2] != it[3] },
        BorderInfoBuilder().down(2).left()
            .down(1, 2).right(2, 2)
            .up(3, 2).left(1, 2).build()
    ),

    /* 0 1 2
     * 3
     */
    L_HORIZONTAL_SHORT_LEFT_BOTTOM(
        arrayOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(0, 1)),
        { it[0] != it[1] && it[0] != it[2] && it[0] != it[3] && it[1] != it[2] },
        BorderInfoBuilder().down(2, 2).right(1, 2)
            .up().right(2)
            .up(1, 2).left(3, 2).build()
    );
}
