package com.holokenmod.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.holokenmod.R;
import com.holokenmod.Utils;

public class StatsActivity extends Activity {
	
	final long[] bestTimeStat = new long[6];
	final long[] avgTimeStat = new long[6];
	final TextView[] timeView = new TextView[6];
	SharedPreferences stats;
	int totalStarted = 0;
	int totalSolved = 0;
	int totalHinted = 0;
	TextView startedGamesView, solvedGamesView, hintedGamesView;
	TextView solvedStreakView, longestStreakView;
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean("showfullscreen", false)) {
			this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		//quick and dirty stats using sharedpref instead of sqlite
		this.stats = getSharedPreferences("stats", MODE_PRIVATE);
		
		setContentView(R.layout.activity_stats);
		timeView[0] = findViewById(R.id.gridtime4);
		timeView[1] = findViewById(R.id.gridtime5);
		timeView[2] = findViewById(R.id.gridtime6);
		timeView[3] = findViewById(R.id.gridtime7);
		timeView[4] = findViewById(R.id.gridtime8);
		timeView[5] = findViewById(R.id.gridtime9);
		
		startedGamesView = findViewById(R.id.startedstat);
		hintedGamesView = findViewById(R.id.hintedstat);
		solvedGamesView = findViewById(R.id.solvedstat);
		solvedStreakView = findViewById(R.id.solvedstreak);
		longestStreakView = findViewById(R.id.longeststreak);
		
		final Button clearStats = findViewById(R.id.clearstats);
		
		clearStats.setOnClickListener(v -> {
			final SharedPreferences.Editor editor = stats.edit();
			editor.clear().commit();
			totalStarted = 0;
			totalSolved = 0;
			totalHinted = 0;
			fillStats();
		});
		
		fillStats();
	}
	
	public void fillStats() {
		for (int i = 0; i < bestTimeStat.length; i++) {
			final int counter = i + 4;
			totalStarted += stats.getInt("playedgames" + counter, 0);
			totalHinted += stats.getInt("hintedgames" + counter, 0);
			totalSolved += stats.getInt("solvedgames" + counter, 0);
			bestTimeStat[i] = stats.getLong("solvedtime" + counter, 0);
			
			final int totalGames = stats.getInt("hintedgames" + counter, 0) +
					stats.getInt("solvedgames" + counter, 0);
			if (totalGames != 0) {
				avgTimeStat[i] = stats.getLong("totaltime" + counter, 0)
						/ totalGames;
			} else {
				avgTimeStat[i] = 0;
			}
			timeView[i].setText(Utils.convertTimetoStr(bestTimeStat[i]) +
					" // " + Utils.convertTimetoStr(avgTimeStat[i]));
		}
		
		double solverate = 0.0;
		if (totalStarted != 0) {
			solverate = totalSolved * 100.0 / totalStarted;
		}
		
		startedGamesView.setText(totalStarted + "");
		hintedGamesView.setText(totalHinted + "");
		solvedGamesView.setText(totalSolved + " (" +
				String.format("%.2f", solverate) + "%)");
		solvedStreakView.setText(stats.getInt("solvedstreak", 0) + "");
		longestStreakView.setText(stats.getInt("longeststreak", 0) + "");
		
	}
}
