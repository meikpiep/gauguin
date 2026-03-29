package org.piepmeyer.gauguin.ui.statistics

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.ActivityStatisticsBinding
import org.piepmeyer.gauguin.history.History
import org.piepmeyer.gauguin.history.HistoryView
import org.piepmeyer.gauguin.preferences.StatisticsManagerReading
import org.piepmeyer.gauguin.ui.ActivityUtils
import org.piepmeyer.gauguin.ui.grid.displayableName

class StatisticsActivity : AppCompatActivity() {
    private val activityUtils: ActivityUtils by inject()
    private val statisticsManager: StatisticsManagerReading by inject()
    private val viewModel: StatisticsViewModel by viewModel()

    private lateinit var binding: ActivityStatisticsBinding

    private lateinit var scatterPlotDiagramFragment: StatisticsScatterPlotDiagramFragment
    private lateinit var difficultyDiagramFragment: StatisticsDifficultyDiagramFragment
    private lateinit var durationDiagramFragment: StatisticsDurationDiagramFragment
    private lateinit var streaksDiagramFragment: StatisticsStreaksDiagramFragment
    private var multiDiagramFragment: StatisticsMultiDiagramFragment? = null

    private var gridSizeChipsHaveBeenCreated: Boolean = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityUtils.configureTheme(this)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        activityUtils.configureMainContainerBackground(binding.root)
        activityUtils.configureRootView(binding.root)

        binding.statisticsClose.setOnClickListener {
            finishAfterTransition()
        }

        binding.clearstats.setOnClickListener { _: View? ->
            resetStatisticsDialog()
        }

        activityUtils.configureFullscreen(this)

        binding.chipSizesAll.setOnClickListener {
            viewModel.viewAllSizes()
        }

        scatterPlotDiagramFragment = StatisticsScatterPlotDiagramFragment()
        difficultyDiagramFragment = StatisticsDifficultyDiagramFragment()
        durationDiagramFragment = StatisticsDurationDiagramFragment()
        streaksDiagramFragment = StatisticsStreaksDiagramFragment()

        supportFragmentManager.commit {
            if (binding.multiDiagramFrame != null) {
                val fragment =
                    StatisticsMultiDiagramFragment(
                        scatterPlotDiagramFragment,
                        durationDiagramFragment,
                    )

                multiDiagramFragment = fragment
                replace(binding.multiDiagramFrame!!.id, fragment)
            }
            /*else {
                binding.scatterPlotCardView?.let { replace(it.id, scatterPlotDiagramFragment) }
                binding.overallDurationCardView?.let { replace(it.id, durationDiagramFragment) }
            }*/

            replace(binding.overallDifficultyCardView.id, difficultyDiagramFragment)
            replace(binding.overallStreaksCardView.id, streaksDiagramFragment)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.historyState.collect {
                    when (it) {
                        is HistoryState.HistoryEmpty -> {
                            binding.noStatisticsAvailableYetCardView.visibility = View.VISIBLE
                            binding.longeststreak.text = "0"
                            binding.solvedstreak.text = "0"
                            binding.startedstat.text = "0"
                            binding.solvedstat.text = "0"
                        }

                        is HistoryState.HistoryLoaded -> {
                            binding.noStatisticsAvailableYetCardView.visibility = View.GONE
                            createSizeChips(it.history)
                            updateHistoryView(it.view)
                        }

                        is HistoryState.HistoryLoading -> {
                            binding.noStatisticsAvailableYetCardView.visibility = View.GONE
                        }
                    }
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.root,
        ) { v, insets ->
            val innerPadding =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout(),
                )
            v.setPadding(
                0,
                0,
                0,
                innerPadding.bottom,
            )

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun createSizeChips(history: History) {
        if (gridSizeChipsHaveBeenCreated) {
            return
        }

        gridSizeChipsHaveBeenCreated = true

        history.gridSizes().sortedBy { it.width }.forEach { gridSize ->
            val chip =
                Chip(
                    binding.sizesChipGroup.context,
                    null,
                    com.google.android.material.R.style.Widget_Material3_Chip_Filter,
                )

            chip.id = View.generateViewId()
            chip.text = gridSize.displayableName(binding.sizesChipGroup.context)
            chip.isCheckable = true
            chip.isClickable = true
            // chip.setTextAppearance(binding.sizesChipGroup.context, com.google.android.material.R.style.Widget_Material3_Chip_Filter)

            binding.sizesChipGroup.addView(chip)

            chip.setOnClickListener {
                viewModel.viewOnlyOneGridSize(gridSize)
            }
        }
    }

    private fun updateHistoryView(view: HistoryView) {
        binding.longeststreak.text = view.longestStreak().toString()
        binding.solvedstreak.text = view.currentStreak().toString()
        binding.startedstat.text = view.playedGrids().toString()
        binding.solvedstat.text = view.numberOfSolvedGrids().toString()
    }

    private fun resetStatisticsDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.statistics_dialog_reset_statistics_title)
            .setMessage(R.string.statistics_dialog_reset_statistics_message)
            .setNegativeButton(
                R.string.statistics_dialog_reset_statistics_cancel_button,
            ) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .setPositiveButton(R.string.statistics_dialog_reset_statistics_ok_button) { _: DialogInterface?, _: Int ->
                run {
                    statisticsManager.clearStatistics()
                    // updateViews()
                }
            }.show()
    }
}
