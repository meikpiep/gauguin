package org.piepmeyer.gauguin.undo

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

class UndoManagerImpl(
    private val gridHolder: () -> Grid,
) : UndoManager {
    private val listeners = mutableListOf<UndoListener>()

    private fun undoSteps() = gridHolder.invoke().undoSteps

    override fun addListener(listener: UndoListener) {
        listeners += listener
    }

    override fun clear() {
        undoSteps().clear()
    }

    override fun saveUndo(
        cell: GridCell,
        batch: Boolean,
    ) {
        val undoStep =
            UndoStep(
                cell,
                cell.userValue,
                cell.possibles,
                batch,
            )
        undoSteps().add(undoStep)

        listeners.forEach { it.undoStateChanged(true) }
    }

    override fun restoreUndo() {
        if (undoSteps().isNotEmpty()) {
            val undoState = undoSteps().removeAt(undoSteps().lastIndex)
            val cell = undoState.cell
            cell.setUserValueIntern(undoState.userValue)
            cell.possibles = undoState.possibles
            cell.isLastModified = true
            if (undoState.isBatch) {
                restoreUndo()
            }
        }
        if (undoSteps().isEmpty()) {
            listeners.forEach { it.undoStateChanged(false) }
        }
    }

    override fun undoPossible() = undoSteps().isNotEmpty()
}
