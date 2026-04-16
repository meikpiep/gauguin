package org.piepmeyer.gauguin.ui.statistics.legacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.Utils
import org.piepmeyer.gauguin.databinding.FragmentLegacyStatisticsDurationDiagramBinding
import org.piepmeyer.gauguin.preferences.StatisticsManagerReading
import org.piepmeyer.gauguin.ui.statistics.FragmentWithClickListenerForAllViews
import kotlin.time.Duration.Companion.seconds

class LegacyStatisticsDurationDiagramFragment :
    Fragment(R.layout.fragment_legacy_statistics_duration_diagram),
    FragmentWithClickListenerForAllViews,
    KoinComponent {
    lateinit var binding: FragmentLegacyStatisticsDurationDiagramBinding

    override var clickListenerForAllViews: View.OnClickListener? = null

    private val statisticsManager: StatisticsManagerReading by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLegacyStatisticsDurationDiagramBinding.inflate(inflater, parent, false)

        val overall = statisticsManager.statistics().overall

        if (overall.solvedDuration.isNotEmpty()) {
            val durationAverage = overall.solvedDurationSum / overall.gamesSolved

            LegacyStatisticsActivity.fillChart(
                binding.overallDuration,
                statisticsManager.statistics().overall.solvedDuration,
                statisticsManager
                    .statistics()
                    .overall.solvedDuration
                    .average(),
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
                Utils.displayableGameDuration(overall.solvedDurationMinimum.seconds)
            binding.overallDurationAverage.text = Utils.displayableGameDuration(durationAverage.seconds)
            binding.overallDurationMaximum.text =
                Utils.displayableGameDuration(overall.solvedDurationMaximum.seconds)
        }

        clickListenerForAllViews?.let { onClickListener ->
            binding.root.allViews.forEach { it.setOnClickListener(onClickListener) }
        }

        return binding.root
    }
}
