package org.piepmeyer.gauguin.undo

import org.piepmeyer.gauguin.grid.GridCell

class UndoManager(private val listener: UndoListener) {
    private val undoList = mutableListOf<UndoState>()

    fun clear() {
        undoList.clear()
    }

    fun saveUndo(
        cell: GridCell,
        batch: Boolean,
    ) {
        val undoState =
            UndoState(
                cell,
                cell.userValue,
                cell.possibles,
                batch,
            )
        undoList.add(undoState)
        listener.undoStateChanged(true)
    }

    fun restoreUndo() {
        if (undoList.isNotEmpty()) {
            val undoState = undoList.removeLast()
            val cell = undoState.cell
            cell.setUserValueIntern(undoState.userValue)
            cell.possibles = undoState.possibles
            cell.isLastModified = true
            if (undoState.isBatch) {
                restoreUndo()
            }
        }
        if (undoList.isEmpty()) {
            listener.undoStateChanged(false)
        }
    }
}
