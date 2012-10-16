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
    long bestTimeStat[] = new long[6];
    long avgTimeStat[] = new long[6];
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
        for (int i=0; i<bestTimeStat.length; i++) {
            int counter = i+4;
            totalStarted += stats.getInt("playedgames"+counter, 0);
            totalHinted += stats.getInt("hintedgames"+counter, 0);
            totalSolved += stats.getInt("solvedgames"+counter, 0);
            bestTimeStat[i] = stats.getLong("solvedtime"+counter, 0);
            
            int totalGames = stats.getInt("hintedgames"+counter, 0) +
                    stats.getInt("solvedgames"+counter, 0);
            if (totalGames != 0)
                avgTimeStat[i] = stats.getLong("totaltime"+counter, 0) 
                        / totalGames;
            else 
                avgTimeStat[i]= 0;
            timeView[i].setText(Utils.convertTimetoStr(bestTimeStat[i]) +
                    " (" + Utils.convertTimetoStr(avgTimeStat[i])+")");
        }

        double solverate = 0.0;
        if (totalStarted != 0)
            solverate = totalSolved*100.0/totalStarted;
        
        startedGamesView.setText(totalStarted + "");
        hintedGamesView.setText(totalHinted + "");
        solvedGamesView.setText(totalSolved + " (" + 
                String.format("%.2f",solverate) + "%)");
    }
}
