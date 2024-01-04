package org.piepmeyer.gauguin.grid

import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
data class GridSize(
    val width: Int,
    val height: Int,
) {
    val surfaceArea: Int
        get() = width * height

    fun smallestSide(): Int = width.coerceAtMost(height)

    fun largestSide(): Int = width.coerceAtLeast(height)

    val amountOfNumbers: Int
        get() = max(width, height)

    val isSquare: Boolean
        get() = width == height
}
