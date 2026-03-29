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
import com.google.android.material.color.MaterialColors
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.views.cartesian.CartesianChartView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.FragmentStatisticsStreaksDiagramBinding
import org.piepmeyer.gauguin.history.HistoryView

class StatisticsStreaksDiagramFragment :
    Fragment(R.layout.fragment_statistics_streaks_diagram),
    FragmentWithClickListenerForAllViews,
    KoinComponent {
    lateinit var binding: FragmentStatisticsStreaksDiagramBinding
    override var clickListenerForAllViews: View.OnClickListener? = null

    private val viewModel: StatisticsViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStatisticsStreaksDiagramBinding.inflate(inflater, parent, false)

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

    private fun updateHistoryView(view: HistoryView) {
        val streakSequence =
            view
                .streaks()
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
