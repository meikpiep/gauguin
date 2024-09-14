package org.piepmeyer.gauguin.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.patrykandpatrick.vico.core.cartesian.axis.AxisPosition
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.Utils
import org.piepmeyer.gauguin.databinding.FragmentStatisticsDurationDiagramBinding
import org.piepmeyer.gauguin.preferences.StatisticsManager
import kotlin.time.Duration.Companion.seconds

class StatisticsDurationDiagramFragment :
    Fragment(R.layout.fragment_statistics_duration_diagram),
    KoinComponent {
    lateinit var binding: FragmentStatisticsDurationDiagramBinding

    private val statisticsManager: StatisticsManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStatisticsDurationDiagramBinding.inflate(inflater, parent, false)

        val overall = statisticsManager.statistics().overall

        if (overall.solvedDuration.isNotEmpty()) {
            val durationAverage = overall.solvedDurationSum / overall.gamesSolved

            StatisticsActivity.fillChart(
                binding.overallDuration,
                statisticsManager.statistics().overall.solvedDuration,
                statisticsManager
                    .statistics()
                    .overall.solvedDuration
                    .average(),
                com.google.android.material.R.attr.colorSecondary,
                com.google.android.material.R.attr.colorOnSecondary,
            )

            val verticalDurationAxis =
                binding.overallDuration
                    .chart!!
                    .startAxis!! as VerticalAxis<AxisPosition.Vertical.Start>

            verticalDurationAxis.valueFormatter =
                CartesianValueFormatter { value, _, _ ->
                    Utils.displayableGameDuration(value.toInt().seconds)
                }

            binding.overallDurationMinimum.text =
                Utils.displayableGameDuration(overall.solvedDurationMinimum.seconds)
            binding.overallDurationAverage.text = Utils.displayableGameDuration(durationAverage.seconds)
            binding.overallDurationMaximum.text =
                Utils.displayableGameDuration(overall.solvedDurationMaximum.seconds)
        }

        return binding.root
    }
}
