package org.piepmeyer.gauguin.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.ActivityStatisticsBinding
import org.piepmeyer.gauguin.preferences.StatisticsManager

class StatisticsActivity : AppCompatActivity() {
    private val activityUtils: ActivityUtils by inject()
    private val statisticeManager: StatisticsManager by inject()

    private lateinit var binding: ActivityStatisticsBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clearstats.setOnClickListener { _: View? ->
            resetStatisticsDialog()
        }

        activityUtils.configureFullscreen(this)

        updateViews()
    }

    private fun updateViews() {
        binding.startedstat.text = statisticeManager.totalStarted().toString()
        binding.hintedstat.text = statisticeManager.totalHinted().toString()
        binding.solvedstat.text = statisticeManager.totalSolved().toString() + " (" +
            String.format(
                "%.2f",
                solveRate(),
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

    private fun resetStatisticsDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_reset_statistics_title)
            .setMessage(R.string.dialog_reset_statistics_msg)
            .setNegativeButton(R.string.dialog_cancel) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .setPositiveButton(R.string.dialog_ok) { _: DialogInterface?, _: Int ->
                run {
                    statisticeManager.clearStatistics()
                    updateViews()
                }
            }
            .show()
    }
}
