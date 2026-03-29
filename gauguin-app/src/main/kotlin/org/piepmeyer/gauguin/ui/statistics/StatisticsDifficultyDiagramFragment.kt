package org.piepmeyer.gauguin.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.FragmentStatisticsDifficultyDiagramBinding
import org.piepmeyer.gauguin.history.HistoryView
import org.piepmeyer.gauguin.ui.statistics.legacy.LegacyStatisticsActivity
import kotlin.math.roundToInt

class StatisticsDifficultyDiagramFragment :
    Fragment(R.layout.fragment_statistics_difficulty_diagram),
    FragmentWithClickListenerForAllViews,
    KoinComponent {
    lateinit var binding: FragmentStatisticsDifficultyDiagramBinding
    override var clickListenerForAllViews: View.OnClickListener? = null

    private val viewModel: StatisticsViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStatisticsDifficultyDiagramBinding.inflate(inflater, parent, false)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.historyState.collect {
                    when (it) {
                        is HistoryState.HistoryLoaded -> {
                            updateHistoryView(it.view)
                            binding.root.visibility = View.VISIBLE
                        }

                        else -> {
                            binding.root.visibility = View.GONE
                        }
                    }
                }
            }
        }

        clickListenerForAllViews?.let { onClickListener ->
            binding.root.allViews.forEach { it.setOnClickListener(onClickListener) }
        }

        return binding.root
    }

    private fun updateHistoryView(historyView: HistoryView) {
        val solvedDifficulty = historyView.solvedGrids().map { it.gridInfo.classicDifficulty }

        if (solvedDifficulty.isNotEmpty()) {
            LegacyStatisticsActivity.fillChart(
                binding.overallDifficulty,
                solvedDifficulty,
                solvedDifficulty.average(),
                com.google.android.material.R.attr.colorOnPrimaryContainer,
            )

            binding.overallDifficultyMinimum.text = solvedDifficulty.min().roundToInt().toString()
            binding.overallDifficultyAverage.text = solvedDifficulty.average().roundToInt().toString()
            binding.overallDifficultyMaximum.text = solvedDifficulty.max().roundToInt().toString()
        }
    }
}
