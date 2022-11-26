package com.holokenmod.undo;

import com.holokenmod.grid.GridCell;

import java.util.LinkedList;

public class UndoManager {
	
	private final LinkedList<UndoState> undoList = new LinkedList<>();
	private final UndoListener listener;
	
	public UndoManager(final UndoListener listener) {
		this.listener = listener;
	}
	
	public void clear() {
		undoList.clear();
	}
	
	public synchronized void saveUndo(final GridCell cell, final boolean batch) {
		final UndoState undoState = new UndoState(cell,
				cell.getUserValue(), cell.getPossibles(), batch);
		undoList.add(undoState);
		
		this.listener.undoStateChanged(true);
	}
	
	public synchronized void restoreUndo() {
		if (!undoList.isEmpty()) {
			final UndoState undoState = undoList.removeLast();
			final GridCell cell = undoState.getCell();
			cell.setUserValue(undoState.getUserValue());
			cell.setPossibles(undoState.getPossibles());
			cell.setLastModified(true);
			
			if (undoState.isBatch()) {
				restoreUndo();
			}
		}
		if (undoList.isEmpty()) {
			this.listener.undoStateChanged(false);
		}
	}
}
