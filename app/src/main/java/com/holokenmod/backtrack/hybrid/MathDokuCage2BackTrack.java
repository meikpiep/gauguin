package com.holokenmod.backtrack.hybrid;

import android.util.Log;

import com.holokenmod.Grid;
import com.holokenmod.GridCage;
import com.holokenmod.GridCell;
import com.holokenmod.creation.GridCageCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MathDokuCage2BackTrack implements BackTrackSolutionListener {
	private final Grid grid;
	private final ArrayList<GridCage> cages;
	private final AtomicInteger solutions = new AtomicInteger(0);
	private final boolean isPreSolved;
	private List<GridCageCreator> cageCreators = new ArrayList<>();
	private int DEPTH_FIRST_PHASE;
	private int[] currentCombination;
	private int sumSolved;
	private ExecutorService threadPool;
	
	public MathDokuCage2BackTrack(Grid grid, boolean isPreSolved) {
		this.grid = grid;
		this.isPreSolved = isPreSolved;
		this.cages = grid.getCages();
		this.sumSolved = 0;
	}
	
	public int solve() {
		cageCreators = cages.parallelStream()
				.map(cage -> new GridCageCreator(grid, cage))
				.collect(Collectors.toList());
		
		if (cages.size() > 4) {
			DEPTH_FIRST_PHASE = cages.size() / 4;
		} else {
			DEPTH_FIRST_PHASE = cages.size() / 2;
		}
		
		currentCombination = new int[DEPTH_FIRST_PHASE];
		
		threadPool = Executors.newFixedThreadPool(
				4,
				new BackTrackThreadFactory(grid, cageCreators, isPreSolved, this));
		
		try {
			solve(0);
		} catch (InterruptedException e) {
		}
		
		Log.d("back2", "Shutdown? " + threadPool.isShutdown());
		
		if (solutions.get() != 2 && !threadPool.isShutdown()) {
			try {
				threadPool.shutdown();
				threadPool.awaitTermination(1, TimeUnit.HOURS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Log.d("back2", "Solved: " + solutions.get() + " combinations: " + sumSolved);
		
		return solutions.get();
	}
	
	public void solve(int cageIndex) throws InterruptedException {
		if (Thread.currentThread().isInterrupted()) {
			throw new InterruptedException();
		}
		
		GridCage cage = cages.get(cageIndex);
		GridCageCreator cageCreator = cageCreators.get(cageIndex);
		
		for (int i = 0; i < cageCreator.getPossibleNums().size(); i++) {
			int[] possibleCombination = cageCreator.getPossibleNums().get(i);
			
			boolean validCells = areCellsValid(cage, possibleCombination);
			
			if (validCells) {
				currentCombination[cageIndex] = i;
				
				int cellNumber = 0;
				
				for(GridCell cell : cage.getCells()) {
					cell.setUserValueIntern(possibleCombination[cellNumber]);
					
					cellNumber++;
				}
				
				//Log.d("backtrack", "Stepping,  " + validCells
				//		+ " constraints " + cageCreator.satisfiesConstraints(possibleCombination)
				//		+ System.lineSeparator() + grid.toStringCellsOnly());
				
				if (cageIndex < DEPTH_FIRST_PHASE - 1) {
					solve(cageIndex + 1);
				} else {
					//Log.d("backtrack", "Found solution " + grid.toString());
					
					if (!threadPool.isShutdown()) {
						try {
							threadPool.submit(new BackTrackRunnable(currentCombination.clone()));
						} catch (RejectedExecutionException e) {
							return;
						}
					}
					
					sumSolved++;
				}
				
				for(GridCell cell : cage.getCells()) {
					cell.setUserValueIntern(-1);
				}
			}
		}
		
		//Log.d("backtrack", "valid combinations: " + sumSolved);
	}
	
	private boolean areCellsValid(GridCage cage, int[] possibleCombination) {
		int i = 0;
		
		for(GridCell cell : cage.getCells()) {
			if (grid.isUserValueUsedInSameRow(cell.getCellNumber(), possibleCombination[i])
					|| grid.isUserValueUsedInSameColumn(cell.getCellNumber(), possibleCombination[i])) {
		//		Log.d("backtrack", "Invalid cell " + cell.getCellNumber()
		//				+  ", value " + possibleCombination[i]);
				return false;
			}
			
			i++;
		}
		
		return true;
	}
	
	@Override
	public synchronized void solutionFound() {
		//Log.i("back2", "Found a solution");
		
		if (solutions.get() == 2) {
			return;
		}
		
		int currentSolutions = solutions.incrementAndGet();
		
		if (currentSolutions == 2) {
			//Log.i("back2", "Found 2 solutions");
			
			threadPool.shutdownNow();
			
			//Thread.currentThread().interrupt();
		}
	}
}