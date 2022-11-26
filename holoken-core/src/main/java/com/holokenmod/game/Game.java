package com.holokenmod.game;

import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridCell;
import com.holokenmod.grid.GridView;
import com.holokenmod.undo.UndoManager;

import java.util.List;

public class Game {
	private Grid grid;
	private GridView gridUI;
	private final UndoManager undoManager;
	private GameSolvedListener solvedListener = null;
	
	public Game(UndoManager undoManager) {
		this.undoManager = undoManager;
	}
	
	public Grid getGrid() {
		return this.grid;
	}
	
	public void setGridUI(GridView gridUI) {
		this.gridUI = gridUI;
	}
	
	public GridView getGridUI() {
		return gridUI;
	}
	
	public synchronized void enterNumber(final int number, final boolean removePossibles) {
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
		if (removePossibles) {
			removePossibles(selectedCell);
		}
		
		if (grid.isActive() && grid.isSolved()) {
			if (grid.getSelectedCell() != null) {
				grid.getSelectedCell().setSelected(false);
				grid.getSelectedCell().getCage().setSelected(false);
			}
			
			grid.setActive(false);
			
			if (this.solvedListener != null) {
				this.solvedListener.puzzleSolved();
			}
		}
		
		gridUI.requestFocus();
		gridUI.invalidate();
	}
	
	public void setSolvedHandler(final GameSolvedListener listener) {
		this.solvedListener = listener;
	}
	
	public synchronized void enterPossibleNumber(final int number) {
		final GridCell selectedCell = grid.getSelectedCell();
		if (!grid.isActive()) {
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
				grid.getPossiblesInRowCol(selectedCell);
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
		final GridCell selectedCell = grid.getSelectedCell();
		if (!grid.isActive()) {
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
		final GridCell selectedCell = grid.getSelectedCell();
		if (!grid.isActive()) {
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
	
	public boolean solveSelectedCage() {
		final GridCell selected = grid.getSelectedCell();
		
		if (selected == null) {
			return false;
		}
		
		grid.solveSelectedCage();
		
		gridUI.invalidate();
		
		return true;
	}
	
	public void solveGrid() {
		grid.solveGrid();
		
		gridUI.invalidate();
	}
	
	public void markInvalidChoices() {
		grid.markInvalidChoices();
		
		gridUI.invalidate();
	}
	
	public boolean revealSelectedCell() {
		final GridCell selectedCell = grid.getSelectedCell();
		
		if (selectedCell == null) {
			return false;
		}
		
		selectedCell.setUserValue(selectedCell.getValue());
		selectedCell.setCheated(true);
		
		gridUI.invalidate();
		
		return true;
	}
	
	public void undoOneStep() {
		clearLastModified();
		undoManager.restoreUndo();
		gridUI.invalidate();
	}
	
	public void restartGame() {
		clearUserValues();
		grid.setActive(true);
	}
}
