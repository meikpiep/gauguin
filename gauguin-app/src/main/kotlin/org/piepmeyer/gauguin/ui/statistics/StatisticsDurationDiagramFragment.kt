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
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.Utils
import org.piepmeyer.gauguin.databinding.FragmentStatisticsDurationDiagramBinding
import org.piepmeyer.gauguin.history.HistoryView
import org.piepmeyer.gauguin.ui.statistics.legacy.LegacyStatisticsActivity
import kotlin.time.Duration.Companion.seconds

class StatisticsDurationDiagramFragment :
    Fragment(R.layout.fragment_statistics_duration_diagram),
    FragmentWithClickListenerForAllViews,
    KoinComponent {
    lateinit var binding: FragmentStatisticsDurationDiagramBinding

    override var clickListenerForAllViews: View.OnClickListener? = null

    private val viewModel: StatisticsViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStatisticsDurationDiagramBinding.inflate(inflater, parent, false)

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
        val solvedDuration = historyView.solvedGrids().map { it.gridInfo.duration.inWholeSeconds }

        if (solvedDuration.isNotEmpty()) {
            LegacyStatisticsActivity.fillChart(
                binding.overallDuration,
                solvedDuration,
                solvedDuration.average(),
                com.google.android.material.R.attr.colorSecondary,
            )

            val axis =
                binding.overallDuration.chart!!.startAxis as VerticalAxis

            binding.overallDuration.chart =
                binding.overallDuration
                    .chart!!
                    .copy(
                        startAxis =
                            axis.copy(
                                valueFormatter = { _, value, _ ->
                                    Utils.displayableGameDuration(value.toInt().seconds)
                                },
                            ),
                    )

            binding.overallDurationMinimum.text =
                Utils.displayableGameDuration(solvedDuration.min().seconds)
            binding.overallDurationAverage.text = Utils.displayableGameDuration(solvedDuration.average().seconds)
            binding.overallDurationMaximum.text =
                Utils.displayableGameDuration(solvedDuration.max().seconds)
        }
    }
}
