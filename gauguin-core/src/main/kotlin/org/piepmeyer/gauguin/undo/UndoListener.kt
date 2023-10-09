package org.piepmeyer.gauguin.undo

fun interface UndoListener {
    fun undoStateChanged(undoPossible: Boolean)
}
