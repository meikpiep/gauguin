package org.piepmeyer.gauguin.creation

import org.piepmeyer.gauguin.grid.Grid

fun interface GridCalculator {
    suspend fun calculate(): Grid
}
