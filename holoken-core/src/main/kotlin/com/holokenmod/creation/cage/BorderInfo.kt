package com.holokenmod.creation.cage

class BorderInfo(
    val direction: Direction,
    val length: Int,
    val offset: Int
) {
    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    companion object {
        fun rectangle(width: Int, height: Int): List<BorderInfo> {
            return listOf(
                BorderInfo(Direction.DOWN, height, 2),
                BorderInfo(Direction.RIGHT, width, 2),
                BorderInfo(Direction.UP, height, 2),
                BorderInfo(Direction.LEFT, width, 2)
            )
        }
    }
}
