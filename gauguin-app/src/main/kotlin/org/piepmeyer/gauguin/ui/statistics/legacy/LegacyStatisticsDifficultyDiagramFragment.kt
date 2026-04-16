package org.piepmeyer.gauguin.ui.statistics.legacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.FragmentLegacyStatisticsDifficultyDiagramBinding
import org.piepmeyer.gauguin.preferences.StatisticsManagerReading
import org.piepmeyer.gauguin.ui.statistics.FragmentWithClickListenerForAllViews
import kotlin.math.roundToInt

class LegacyStatisticsDifficultyDiagramFragment :
    Fragment(R.layout.fragment_legacy_statistics_difficulty_diagram),
    FragmentWithClickListenerForAllViews,
    KoinComponent {
    lateinit var binding: FragmentLegacyStatisticsDifficultyDiagramBinding
    override var clickListenerForAllViews: View.OnClickListener? = null

    private val statisticsManager: StatisticsManagerReading by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLegacyStatisticsDifficultyDiagramBinding.inflate(inflater, parent, false)

        val overall = statisticsManager.statistics().overall

        if (overall.solvedDifficulty.isNotEmpty()) {
            val difficultyAverage = overall.solvedDifficultySum / overall.gamesSolved

            LegacyStatisticsActivity.fillChart(
                binding.overallDifficulty,
                statisticsManager.statistics().overall.solvedDifficulty,
                statisticsManager
                    .statistics()
                    .overall.solvedDifficulty
                    .average(),
                com.google.android.material.R.attr.colorOnPrimaryContainer,
            )

            binding.overallDifficultyMinimum.text = overall.solvedDifficultyMinimum.roundToInt().toString()
            binding.overallDifficultyAverage.text = difficultyAverage.roundToInt().toString()
            binding.overallDifficultyMaximum.text = overall.solvedDifficultyMaximum.roundToInt().toString()
        }

        clickListenerForAllViews?.let { onClickListener ->
            binding.root.allViews.forEach { it.setOnClickListener(onClickListener) }
        }

        return binding.root
    }
}
