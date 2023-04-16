package com.holokenmod.undo

import com.holokenmod.grid.GridCell

data class UndoState(
    val cell: GridCell,
    val userValue: Int,
    val possibles: Set<Int>,
    val isBatch: Boolean,
)
