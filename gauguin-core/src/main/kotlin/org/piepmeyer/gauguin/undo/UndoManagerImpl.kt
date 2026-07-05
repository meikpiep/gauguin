package org.piepmeyer.gauguin.undo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridCell

class UndoManagerImpl(
    private val gridHolder: () -> Grid,
) : UndoManager {
    private val mutableUndoPossibleState = MutableStateFlow(false)
    override val undoPossibleState: StateFlow<Boolean> = mutableUndoPossibleState.asStateFlow()

    private fun undoSteps() = gridHolder.invoke().undoSteps

    override fun clear() {
        undoSteps().clear()

        mutableUndoPossibleState.value = false
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

        mutableUndoPossibleState.value = true
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
        mutableUndoPossibleState.value = undoSteps().isNotEmpty()
    }
}
