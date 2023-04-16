package com.holokenmod.undo

interface UndoListener {
    fun undoStateChanged(undoPossible: Boolean)
}