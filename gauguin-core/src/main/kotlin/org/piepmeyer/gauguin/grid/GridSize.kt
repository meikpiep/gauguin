package org.piepmeyer.gauguin.grid

import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
data class GridSize(
    val width: Int,
    val height: Int
) {
    val surfaceArea: Int
        get() = width * height

    override fun toString(): String {
        return width.toString() + "x" + height
    }

    fun smallestSide(): Int = width.coerceAtMost(height)
    fun largestSide(): Int = width.coerceAtLeast(height)

    val amountOfNumbers: Int
        get() = max(width, height)

    val isSquare: Boolean
        get() = width == height

    companion object {
        fun create(gridSizeString: String): GridSize {
            return try {
                val size = gridSizeString.toInt()
                GridSize(size, size)
            } catch (e: NumberFormatException) {
                val parts = gridSizeString.split("x")
                val width = parts[0].toInt()
                val height = parts[1].toInt()
                GridSize(width, height)
            }
        }
    }
}
