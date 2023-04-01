package com.holokenmod.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.holokenmod.R

class StatsActivity : AppCompatActivity() {
    private var stats: SharedPreferences? = null
    private var totalStarted = 0
    private var totalSolved = 0
    private var totalHinted = 0
    private var startedGamesView: TextView? = null
    private var solvedGamesView: TextView? = null
    private var hintedGamesView: TextView? = null
    private var solvedStreakView: TextView? = null
    private var longestStreakView: TextView? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        if (!PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("showfullscreen", false)
        ) {
            this.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            this.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        //quick and dirty stats using sharedpref instead of sqlite
        stats = getSharedPreferences("stats", MODE_PRIVATE)
        setContentView(R.layout.activity_stats)
        startedGamesView = findViewById(R.id.startedstat)
        hintedGamesView = findViewById(R.id.hintedstat)
        solvedGamesView = findViewById(R.id.solvedstat)
        solvedStreakView = findViewById(R.id.solvedstreak)
        longestStreakView = findViewById(R.id.longeststreak)
        val clearStats = findViewById<Button>(R.id.clearstats)
        clearStats.setOnClickListener { v: View? ->
            val editor = stats!!.edit()
            editor.clear().commit()
            totalStarted = 0
            totalSolved = 0
            totalHinted = 0
            fillStats()
        }
        fillStats()
    }

    private fun fillStats() {
        var solverate = 0.0
        if (totalStarted != 0) {
            solverate = totalSolved * 100.0 / totalStarted
        }
        startedGamesView!!.text = totalStarted.toString() + ""
        hintedGamesView!!.text = totalHinted.toString() + ""
        solvedGamesView!!.text = "$totalSolved (" + String.format(
            "%.2f",
            solverate
        ) + "%)"
        solvedStreakView!!.text = stats!!.getInt("solvedstreak", 0).toString() + ""
        longestStreakView!!.text = stats!!.getInt("longeststreak", 0).toString() + ""
    }
}