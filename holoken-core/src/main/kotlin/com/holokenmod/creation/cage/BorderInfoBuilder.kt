package com.holokenmod.creation.cage

class BorderInfoBuilder {
    private val infos = mutableListOf<BorderInfo>()

    fun up(length: Int = 1, offset: Int = 0): BorderInfoBuilder {
        infos.add(BorderInfo(BorderInfo.Direction.UP, length, offset))

        return this
    }

    fun down(length: Int = 1, offset: Int = 0): BorderInfoBuilder {
        infos.add(BorderInfo(BorderInfo.Direction.DOWN, length, offset))

        return this
    }

    fun left(length: Int = 1, offset: Int = 0): BorderInfoBuilder {
        infos.add(BorderInfo(BorderInfo.Direction.LEFT, length, offset))

        return this
    }

    fun right(length: Int = 1, offset: Int = 0): BorderInfoBuilder {
        infos.add(BorderInfo(BorderInfo.Direction.RIGHT, length, offset))

        return this
    }

    fun build(): List<BorderInfo> = infos
}
