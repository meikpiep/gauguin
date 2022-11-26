package com.holokenmod;

import android.content.Context;
import android.content.SharedPreferences;

import com.holokenmod.grid.Grid;
import com.holokenmod.grid.GridSize;

import java.util.Optional;

public class StatisticsManager {
	private final SharedPreferences stats;
	private final Grid grid;
	
	public StatisticsManager(Context context, Grid grid) {
		this.grid = grid;
		this.stats = context.getSharedPreferences("stats", Context.MODE_PRIVATE);
	}
	
	public void storeStatisticsAfterNewGame() {
		final int gamestat = stats
				.getInt("playedgames" + grid.getGridSize(), 0);
		final SharedPreferences.Editor editor = stats.edit();
		editor.putInt("playedgames" + grid.getGridSize(), gamestat + 1);
		editor.commit();
	}
	
	public Optional<String> storeStatisticsAfterFinishedGame() {
		final GridSize gridsize = grid.getGridSize();
		
		// assess hint penalty - gridsize^2/2 seconds for each cell
		final long penalty = (long) grid.countCheated() * 500 * gridsize.getSurfaceArea();
		
		grid.setPlayTime(grid.getPlayTime() + penalty);
		final long solvetime = grid.getPlayTime();
		String solveStr = Utils.convertTimetoStr(solvetime);
		
		final int hintedstat = stats.getInt("hintedgames" + gridsize, 0);
		final int solvedstat = stats.getInt("solvedgames" + gridsize, 0);
		final long timestat = stats.getLong("solvedtime" + gridsize, 0);
		final long totaltimestat = stats.getLong("totaltime" + gridsize, 0);
		final SharedPreferences.Editor editor = stats.edit();
		
		if (penalty != 0) {
			editor.putInt("hintedgames" + gridsize, hintedstat + 1);
			solveStr += "^";
		} else {
			editor.putInt("solvedgames" + gridsize, solvedstat + 1);
		}
		
		editor.putLong("totaltime" + gridsize, totaltimestat + solvetime);
		
		Optional<String> recordTime;
		
		if (timestat == 0 || timestat > solvetime) {
			editor.putLong("solvedtime" + gridsize, solvetime);
			
			recordTime = Optional.of(solveStr);
		} else {
			recordTime = Optional.empty();
		}
		editor.commit();
		
		return recordTime;
	}
	
	public void storeStreak(final boolean isSolved) {
		final int solved_streak = stats.getInt("solvedstreak", 0);
		final int longest_streak = stats.getInt("longeststreak", 0);
		final SharedPreferences.Editor editor = stats.edit();
		
		if (isSolved) {
			editor.putInt("solvedstreak", solved_streak + 1);
			if (solved_streak == longest_streak) {
				editor.putInt("longeststreak", solved_streak + 1);
			}
		} else {
			editor.putInt("solvedstreak", 0);
		}
		editor.commit();
	}
}
