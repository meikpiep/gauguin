package org.piepmeyer.gauguin.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.StatisticsManager
import org.piepmeyer.gauguin.databinding.ActivityStatisticsBinding

class StatsActivity : AppCompatActivity() {
    private val activityUtils: ActivityUtils by inject()

    private lateinit var binding: ActivityStatisticsBinding

    private lateinit var statisticeManager: StatisticsManager

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        statisticeManager = StatisticsManager(this)

        binding.clearstats.setOnClickListener { _: View? ->
            statisticeManager.clearStatistics()
            updateViews()
        }

        activityUtils.configureFullscreen(this)

        updateViews()
    }

    private fun updateViews() {
        binding.startedstat.text = statisticeManager.totalStarted().toString()
        binding.hintedstat.text = statisticeManager.totalHinted().toString()
        binding.solvedstat.text = statisticeManager.totalSolved().toString() + " (" + String.format(
            "%.2f",
            solveRate()
        ) + "%)"
        binding.solvedstreak.text = statisticeManager.currentStreak().toString()
        binding.longeststreak.text = statisticeManager.longestStreak().toString()
    }

    private fun solveRate(): Double {
        return if (statisticeManager.totalStarted() == 0) {
            0.0
        } else {
            statisticeManager.totalSolved() * 100.0 / statisticeManager.totalStarted()
        }
    }
}