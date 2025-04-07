package org.piepmeyer.gauguin.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.color.MaterialColors
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.views.cartesian.CartesianChartView
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
            statisticsManager
                .statistics()
                .overall.streakSequence
                .map {
                    if (it == 0) {
                        0.1.toFloat()
                    } else {
                        it.toFloat()
                    }
                }.toMutableList()

        val filteredStreaks =
            streakSequence.filterIndexed { index, number ->
                (index == streakSequence.size - 1 || index == 0) ||
                    number == 0f ||
                    (streakSequence[index - 1] >= number || streakSequence[index + 1] <= number)
            }

        val filledUpStreakSequence =
            if (filteredStreaks.size >= 8) {
                filteredStreaks
            } else {
                filteredStreaks + List(8 - filteredStreaks.size) { 0F }
            }

        binding.overallStreaks.setModel(
            CartesianChartModel(
                ColumnCartesianLayerModel.build {
                    series(filledUpStreakSequence)
                },
            ),
        )

        addColorToLine(binding.overallStreaks, filteredStreaks.size - 1)

        return binding.root
    }

    private fun addColorToLine(
        chartView: CartesianChartView,
        indexCurrentStreak: Int,
    ) {
        val formerStreaksColumn =
            LineComponent(
                color = MaterialColors.getColor(binding.overallStreaks, com.google.android.material.R.attr.colorSecondary),
                thicknessDp = 8f,
            )
        val currentStreakColumn =
            LineComponent(
                color = MaterialColors.getColor(binding.overallStreaks, R.attr.colorCustomColor1),
                thicknessDp = 8f,
            )

        with(chartView) {
            chart =
                chart!!.copy(
                    (chart!!.layers[0] as ColumnCartesianLayer).copy(
                        columnProvider = getColumnProvider(indexCurrentStreak, formerStreaksColumn, currentStreakColumn),
                    ),
                )
        }
    }

    private fun getColumnProvider(
        indexCurrentStreak: Int,
        formerStreaksColumn: LineComponent,
        currentStreakColumn: LineComponent,
    ) = object : ColumnCartesianLayer.ColumnProvider {
        override fun getColumn(
            entry: ColumnCartesianLayerModel.Entry,
            seriesIndex: Int,
            extraStore: ExtraStore,
        ) = if (entry.x.toInt() == indexCurrentStreak) currentStreakColumn else formerStreaksColumn

        override fun getWidestSeriesColumn(
            seriesIndex: Int,
            extraStore: ExtraStore,
        ) = formerStreaksColumn
    }
}
