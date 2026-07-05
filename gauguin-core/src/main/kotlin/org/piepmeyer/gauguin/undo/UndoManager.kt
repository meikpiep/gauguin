package org.piepmeyer.gauguin.undo

import kotlinx.coroutines.flow.StateFlow
import org.piepmeyer.gauguin.grid.GridCell

interface UndoManager {
    val undoPossibleState: StateFlow<Boolean>

    fun clear()

    fun saveUndo(
        cell: GridCell,
        batch: Boolean,
    )

    fun restoreUndo()
}
