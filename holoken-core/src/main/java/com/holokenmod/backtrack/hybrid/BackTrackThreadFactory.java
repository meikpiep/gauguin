package com.holokenmod.backtrack.hybrid;

import com.holokenmod.grid.Grid;
import com.holokenmod.creation.GridSingleCageCreator;

import java.util.List;
import java.util.concurrent.ThreadFactory;

public class BackTrackThreadFactory implements ThreadFactory {
	private final Grid grid;
	private final List<GridSingleCageCreator> cageCreators;
	private final BackTrackSolutionListener solutionListener;
	private final boolean isPreSolved;
	
	public BackTrackThreadFactory(Grid grid, List<GridSingleCageCreator> cageCreators, boolean isPreSolved, BackTrackSolutionListener solutionListener) {
		this.grid = grid;
		this.cageCreators = cageCreators;
		this.isPreSolved = isPreSolved;
		this.solutionListener = solutionListener;
	}
	
	@Override
	public Thread newThread(Runnable r) {
		return new BackTrackThread(r, createGrid(), cageCreators, isPreSolved, solutionListener);
	}
	
	private Grid createGrid() {
		return grid.copyEmpty();
	}
}
