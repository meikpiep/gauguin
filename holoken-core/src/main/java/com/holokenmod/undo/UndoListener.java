package com.holokenmod.undo;

public interface UndoListener {
	void undoStateChanged(boolean undoPossible);
}
