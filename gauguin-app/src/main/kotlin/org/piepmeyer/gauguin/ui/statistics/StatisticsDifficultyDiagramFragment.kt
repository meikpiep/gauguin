package org.piepmeyer.gauguin.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.FragmentStatisticsDifficultyDiagramBinding
import org.piepmeyer.gauguin.preferences.StatisticsManager
import kotlin.math.roundToInt

class StatisticsDifficultyDiagramFragment :
    Fragment(R.layout.fragment_statistics_difficulty_diagram),
    KoinComponent {
    lateinit var binding: FragmentStatisticsDifficultyDiagramBinding

    private val statisticsManager: StatisticsManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStatisticsDifficultyDiagramBinding.inflate(inflater, parent, false)

        val overall = statisticsManager.statistics().overall

        if (overall.solvedDifficulty.isNotEmpty()) {
            val difficultyAverage = overall.solvedDifficultySum / overall.gamesSolved

            StatisticsActivity.fillChart(
                binding.overallDifficulty,
                statisticsManager.statistics().overall.solvedDifficulty,
                statisticsManager
                    .statistics()
                    .overall.solvedDifficulty
                    .average(),
                com.google.android.material.R.attr.colorPrimary,
                com.google.android.material.R.attr.colorOnPrimary,
            )

            binding.overallDifficultyMinimum.text = overall.solvedDifficultyMinimum.roundToInt().toString()
            binding.overallDifficultyAverage.text = difficultyAverage.roundToInt().toString()
            binding.overallDifficultyMaximum.text = overall.solvedDifficultyMaximum.roundToInt().toString()
        }

        return binding.root
    }
}
