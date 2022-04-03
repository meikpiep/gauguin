package com.holokenmod;

import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.ui.GridUI;

import java.util.List;

public class Game {
	private Grid grid;
	private GridUI gridUI;
	private final UndoManager undoManager;
	private GridUI.OnSolvedListener solvedListener = null;
	
	public Game(UndoManager undoManager) {
		this.undoManager = undoManager;
	}
	
	public Grid getGrid() {
		return this.grid;
	}
	
	public void setGridUI(GridUI gridUI) {
		this.gridUI = gridUI;
	}
	
	public GridUI getGridUI() {
		return gridUI;
	}
	
	public synchronized void enterNumber(final int number) {
		final GridCell selectedCell = grid.getSelectedCell();
		if (!grid.isActive()) {
			return;
		}
		if (selectedCell == null) {
			return;
		}
		clearLastModified();
		
		undoManager.saveUndo(selectedCell, false);
		
		selectedCell.setUserValue(number);
		if (ApplicationPreferences.getInstance().removePencils()) {
			removePossibles(selectedCell);
		}
		
		if (grid.isActive() && grid.isSolved()) {
			if (this.solvedListener != null) {
				this.solvedListener.puzzleSolved();
			}
		}
		
		gridUI.requestFocus();
		gridUI.invalidate();
	}
	
	public void setSolvedHandler(final GridUI.OnSolvedListener listener) {
		this.solvedListener = listener;
	}
	
	public synchronized void enterPossibleNumber(final int number) {
		final GridCell selectedCell = getGrid().getSelectedCell();
		if (!getGrid().isActive()) {
			return;
		}
		if (selectedCell == null) {
			return;
		}
		clearLastModified();
		
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
	
	public void setGrid(Grid grid) {
		this.grid = grid;
	}
	
	public void selectCell() {
		final GridCell selectedCell = getGrid().getSelectedCell();
		if (!getGrid().isActive()) {
			return;
		}
		if (selectedCell == null) {
			return;
		}
		
		gridUI.requestFocus();
		gridUI.invalidate();
	}
	
	public void eraseSelectedCell() {
		final GridCell selectedCell = grid.getSelectedCell();
		
		if (!grid.isActive()) {
			return;
		}
		if (selectedCell == null) {
			return;
		}
		
		if (selectedCell.isUserValueSet() || selectedCell.getPossibles().size() > 0) {
			clearLastModified();
			undoManager.saveUndo(selectedCell, false);
			selectedCell.clearUserValue();
		}
	}
	
	public boolean setSinglePossibleOnSelectedCell(boolean rmpencil) {
		final GridCell selectedCell = getGrid().getSelectedCell();
		if (!getGrid().isActive()) {
			return false;
		}
		if (selectedCell == null) {
			return false;
		}
		
		if (selectedCell.getPossibles().size() == 1) {
			clearLastModified();
			undoManager.saveUndo(selectedCell, false);
			selectedCell.setUserValue(selectedCell.getPossibles().iterator().next());
			
			if (rmpencil) {
				removePossibles(selectedCell);
			}
		}
		
		gridUI.requestFocus();
		gridUI.invalidate();
		
		return true;
	}
	
	public void clearUserValues() {
		grid.clearUserValues();
		
		gridUI.invalidate();
	}
	
	public void clearLastModified() {
		grid.clearLastModified();
		
		gridUI.invalidate();
	}
	
	public void solveSelectedCage() {
		grid.solveSelectedCage();
		
		gridUI.invalidate();
	}
	
	public void solveGrid() {
		grid.solveGrid();
		
		gridUI.invalidate();
	}
	
	public void markInvalidChoices() {
		grid.markInvalidChoices();
		
		gridUI.invalidate();
	}
}
