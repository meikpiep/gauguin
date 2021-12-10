package com.holokenmod.backtrack.hybrid;

import com.holokenmod.Grid;
import com.holokenmod.creation.GridCageCreator;

import java.util.List;

public class BackTrackThread extends Thread {
	public final List<GridCageCreator> cageCreators;
	public final Grid grid;
	public final BackTrackSolutionListener solutionListener;
	public final boolean isPreSolved;
	
	public BackTrackThread(Runnable r, Grid grid, List<GridCageCreator> cageCreators, boolean isPreSolved, BackTrackSolutionListener solutionListener) {
		super(r);
		this.grid = grid;
		this.cageCreators = cageCreators;
		this.isPreSolved = isPreSolved;
		this.solutionListener = solutionListener;
	}
	
	@Override
	public void run() {
		super.run();
	}
}
