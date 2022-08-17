package com.holokenmod.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.holokenmod.R;

public class StatsActivity extends AppCompatActivity {
	
	SharedPreferences stats;
	int totalStarted = 0;
	int totalSolved = 0;
	int totalHinted = 0;
	TextView startedGamesView, solvedGamesView, hintedGamesView;
	TextView solvedStreakView, longestStreakView;
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		
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
