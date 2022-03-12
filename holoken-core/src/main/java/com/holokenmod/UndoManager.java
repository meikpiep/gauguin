package com.holokenmod;

import android.view.View;

import java.util.LinkedList;

public class UndoManager {
	
	private final LinkedList<UndoState> undoList = new LinkedList<>();
	private final View actionUndo;
	private int indexOfUndoBookmark;
	
	public UndoManager(final View actionUndo) {
		this.actionUndo = actionUndo;
	}
	
	public void clear() {
		undoList.clear();
	}
	
	public synchronized void saveUndo(final GridCell cell, final boolean batch) {
		final UndoState undoState = new UndoState(cell,
				cell.getUserValue(), cell.getPossibles(), cell.isFlaky(), batch);
		undoList.add(undoState);
		this.actionUndo.setEnabled(true);
	}
	
	public synchronized void restoreUndo() {
		if (!undoList.isEmpty()) {
			final UndoState undoState = undoList.removeLast();
			final GridCell cell = undoState.getCell();
			cell.setUserValue(undoState.getUserValue());
			cell.setPossibles(undoState.getPossibles());
			cell.setLastModified(true);
			cell.setFlaky(undoState.isFlaky());
			
			if (undoState.isBatch()) {
				restoreUndo();
			}
		}
		if (undoList.isEmpty()) {
			this.actionUndo.setEnabled(false);
		}
	}
	
	public void saveBookmark() {
		indexOfUndoBookmark = undoList.size();
	}
	
	public void restoreBookmark() {
		while (undoList.size() > indexOfUndoBookmark) {
			restoreUndo();
		}
	}
}
