package org.piepmeyer.gauguin.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.FragmentStatisticsStreaksDiagramBinding
import org.piepmeyer.gauguin.preferences.StatisticsManager

class StatisticsStreaksDiagramFragment :
    Fragment(R.layout.fragment_statistics_streaks_diagram),
    KoinComponent {
    lateinit var binding: FragmentStatisticsStreaksDiagramBinding

    private val statisticsManager: StatisticsManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStatisticsStreaksDiagramBinding.inflate(inflater, parent, false)

        val streakSequence =
            statisticsManager.statistics().overall.streakSequence.map {
                if (it == 0) {
                    0.1
                } else {
                    it
                }
            }

        val filledUpStreakSequence =
            if (streakSequence.size >= 8) {
                streakSequence
            } else {
                streakSequence + List(8 - streakSequence.size) { 0F }
            }

        binding.overallStreaks.setModel(
            CartesianChartModel(
                ColumnCartesianLayerModel.build {
                    series(filledUpStreakSequence)
                },
            ),
        )

        return binding.root
    }
}
