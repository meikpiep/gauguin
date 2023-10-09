package org.piepmeyer.gauguin.undo

import org.piepmeyer.gauguin.grid.GridCell

data class UndoState(
    val cell: GridCell,
    val userValue: Int,
    val possibles: Set<Int>,
    val isBatch: Boolean
)
