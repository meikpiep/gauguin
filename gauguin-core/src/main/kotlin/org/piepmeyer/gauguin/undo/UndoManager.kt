package org.piepmeyer.gauguin.undo

import org.piepmeyer.gauguin.grid.GridCell

interface UndoManager {
    fun addListener(listener: UndoListener)

    fun clear()

    fun saveUndo(
        cell: GridCell,
        batch: Boolean,
    )

    fun restoreUndo()

    fun undoPossible(): Boolean
}
