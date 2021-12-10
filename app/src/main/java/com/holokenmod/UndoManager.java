package com.holokenmod;

import android.view.View;
import android.widget.ImageButton;

import java.util.LinkedList;

public class UndoManager {
	
	private final LinkedList<UndoState> undoList = new LinkedList<>();
	private final ImageButton actionUndo;
	
	public UndoManager(final ImageButton actionUndo) {
		this.actionUndo = actionUndo;
	}
	
	public void clear() {
		undoList.clear();
	}
	
	public synchronized void saveUndo(final GridCell cell, final boolean batch) {
		final UndoState undoState = new UndoState(cell,
				cell.getUserValue(), cell.getPossibles(), batch);
		undoList.add(undoState);
		this.actionUndo.setVisibility(View.VISIBLE);
	}
	
	public synchronized void restoreUndo() {
		if (!undoList.isEmpty()) {
			final UndoState undoState = undoList.removeLast();
			final GridCell cell = undoState.getCell();
			cell.setUserValue(undoState.getUserValue());
			cell.setPossibles(undoState.getPossibles());
			cell.setLastModified(true);
			if (undoState.getBatch()) {
				restoreUndo();
			}
		}
		if (undoList.isEmpty()) {
			this.actionUndo.setVisibility(View.INVISIBLE);
		}
	}
}
