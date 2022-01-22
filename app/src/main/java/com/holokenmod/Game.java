package com.holokenmod;

import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.ui.GridUI;

import java.util.List;

public class Game {
	private final Grid grid;
	private final GridUI gridUI;
	private final UndoManager undoManager;
	
	public Game(Grid grid, GridUI gridUI, UndoManager undoManager) {
		this.grid = grid;
		this.gridUI = gridUI;
		this.undoManager = undoManager;
	}
	
	public Grid getGrid() {
		return this.grid;
	}
	
	public GridUI getGridUI() {
		return this.gridUI;
	}
	
	public UndoManager getUndoList() {
		return undoManager;
	}
	
	public synchronized void enterNumber(final int number) {
		final GridCell selectedCell = grid.getSelectedCell();
		if (!grid.isActive()) {
			return;
		}
		if (selectedCell == null) {
			return;
		}
		gridUI.clearLastModified();
		
		undoManager.saveUndo(selectedCell, false);
		
		selectedCell.setUserValue(number);
		if (ApplicationPreferences.getInstance().removePencils()) {
			removePossibles(selectedCell);
		}
		
		gridUI.requestFocus();
		gridUI.invalidate();
	}
	
	public synchronized void enterPossibleNumber(final int number) {
		final GridCell selectedCell = getGrid().getSelectedCell();
		if (!getGrid().isActive()) {
			return;
		}
		if (selectedCell == null) {
			return;
		}
		gridUI.clearLastModified();
		
		undoManager.saveUndo(selectedCell, false);
		
		if (selectedCell.isUserValueSet()) {
			final int oldValue = selectedCell.getUserValue();
			selectedCell.clearUserValue();
			selectedCell.togglePossible(oldValue);
		}
		
		selectedCell.togglePossible(number);
		
		gridUI.requestFocus();
		gridUI.invalidate();
	}
	
	public void removePossibles(final GridCell selectedCell) {
		final List<GridCell> possibleCells =
				getGrid().getPossiblesInRowCol(selectedCell);
		for (final GridCell cell : possibleCells) {
			undoManager.saveUndo(cell, true);
			cell.setLastModified(true);
			cell.removePossible(selectedCell.getUserValue());
		}
	}
}
