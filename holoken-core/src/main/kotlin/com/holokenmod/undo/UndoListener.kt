package com.holokenmod.undo

fun interface UndoListener {
    fun undoStateChanged(undoPossible: Boolean)
}
