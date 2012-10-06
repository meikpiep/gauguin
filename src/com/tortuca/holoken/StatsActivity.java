package com.tortuca.holoken;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class StatsActivity extends Activity {
	
	SharedPreferences stats;
	long timestat[] = new long[6];
    int totalStarted = 0;
    int totalSolved = 0;
    int totalHinted = 0;

	TextView timeView[] = new TextView[6];
	TextView startedGamesView, solvedGamesView, hintedGamesView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("showfullscreen", false))
	    	this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    else
	    	this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
	    
	    //quick and dirty stats using sharedpref instead of sqlite
        this.stats = getSharedPreferences("stats", MODE_PRIVATE);
        
	    setContentView(R.layout.activity_stats);
	    timeView[0] = (TextView)findViewById(R.id.gridtime4);
	    timeView[1] = (TextView)findViewById(R.id.gridtime5);
	    timeView[2] = (TextView)findViewById(R.id.gridtime6);
	    timeView[3] = (TextView)findViewById(R.id.gridtime7);
	    timeView[4] = (TextView)findViewById(R.id.gridtime8);
	    timeView[5] = (TextView)findViewById(R.id.gridtime9);
	    startedGamesView = (TextView)findViewById(R.id.startedstat);
	    hintedGamesView = (TextView)findViewById(R.id.hintedstat);
	    solvedGamesView = (TextView)findViewById(R.id.solvedstat);

		Button clearStats = (Button)findViewById(R.id.clearstats);
		clearStats.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
		        SharedPreferences.Editor editor = stats.edit();
		        editor.clear().commit();
		        totalStarted = 0;
		        totalSolved = 0;
		        totalHinted = 0;
		        fillStats();
    		}
    	});

        fillStats();
	}
	
	
	public void fillStats() {
        for (int i=0; i<timestat.length; i++) {
        	int counter = i+4;
	        totalStarted += stats.getInt("playedgames"+counter, 0);
	        totalHinted += stats.getInt("hintedgames"+counter, 0);
	        totalSolved += stats.getInt("solvedgames"+counter, 0);
	        timestat[i] = stats.getLong("solvedtime"+counter, 0);
	        convertTimetoStr(timestat[i],timeView[i]);
        }
        double solverate = 0.0;
        if (totalStarted != 0)
        	solverate = totalSolved*100.0/totalStarted;
        
        startedGamesView.setText(totalStarted + "");
        hintedGamesView.setText(totalHinted + "");
        solvedGamesView.setText(totalSolved + " (" + 
        		String.format("%.2f",solverate) + "%)");
	}
	
	public void convertTimetoStr(long time, TextView tv) {
        int seconds = (int) (time / 1000);
        int minutes = seconds / 60 % 60;
        int hours   = seconds / 3600;
        seconds     = seconds % 60;

        tv.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
	}
}
