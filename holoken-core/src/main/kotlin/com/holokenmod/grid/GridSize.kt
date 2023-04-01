package com.holokenmod.grid

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import kotlin.math.max

class GridSize(val width: Int, val height: Int) {

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val gridSize = o as GridSize
        return EqualsBuilder().append(width, gridSize.width)
            .append(height, gridSize.height).isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(17, 37).append(width).append(height).toHashCode()
    }

    val surfaceArea: Int
        get() = width * height

    override fun toString(): String {
        return width.toString() + "x" + height
    }

    val amountOfNumbers: Int
        get() = max(width, height)

    val isSquare: Boolean
        get() = width == height

    companion object {
        @JvmStatic
        fun create(gridSizeString: String): GridSize {
            if (StringUtils.isNumeric(gridSizeString)) {
                val size = gridSizeString.toInt()
                return GridSize(size, size)
            }
            val parts = StringUtils.split(gridSizeString, "x")
            val width = parts[0].toInt()
            val height = parts[1].toInt()
            return GridSize(width, height)
        }
    }
}