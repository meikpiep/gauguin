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
import com.androidplot.util.PixelUtils
import com.androidplot.xy.BoundaryMode
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYGraphWidget
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.component.KoinComponent
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.Utils
import org.piepmeyer.gauguin.databinding.FragmentStatisticsScatterPlotDiagramBinding
import org.piepmeyer.gauguin.history.HistoryView
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import kotlin.math.nextUp
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

class StatisticsScatterPlotDiagramFragment :
    Fragment(R.layout.fragment_statistics_scatter_plot_diagram),
    FragmentWithClickListenerForAllViews,
    KoinComponent {
    lateinit var binding: FragmentStatisticsScatterPlotDiagramBinding

    override var clickListenerForAllViews: View.OnClickListener? = null

    private val viewModel: StatisticsViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStatisticsScatterPlotDiagramBinding.inflate(inflater, parent, false)

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
        if (historyView.solvedGrids().isNotEmpty()) {
            createPlot(historyView)
        }
    }

    private fun createPlot(historyView: HistoryView) {
        val difficultyDurationMap =
            historyView
                .solvedGrids()
                .associate { Pair(it.gridInfo.classicDifficulty, it.gridInfo.duration) }
                .toMutableMap()

        val maximumDuration =
            difficultyDurationMap.values
                .max()
                .inWholeSeconds
                .toInt()
                .coerceAtLeast(60)
        val roundedMaximumDuration = ((maximumDuration * 1.2) / 60.0).nextUp().roundToInt() * 60

        val maximumDifficulty =
            difficultyDurationMap.keys
                .max()
                .roundToInt()
                .coerceAtLeast(10)
        val roundedMaximumDifficulty = ((maximumDifficulty * 1.2) / 20.0).nextUp().roundToInt() * 20

        val overallSeries = SimpleXYSeries(null)
        val lastItemSeries = SimpleXYSeries(null)

        if (historyView.viewContainsMostRecentSolvedGrid) {
            val lastEntry = difficultyDurationMap.entries.last()
            difficultyDurationMap.remove(lastEntry.key)

            lastItemSeries.addLast(lastEntry.value.inWholeSeconds, lastEntry.key)
        }

        difficultyDurationMap.forEach { (difficulty, duration) ->
            overallSeries.addLast(duration.inWholeSeconds, difficulty)
        }

        PixelUtils.init(context)

        val formatter = LineAndPointFormatter()
        formatter.isLegendIconEnabled = false
        formatter.fillPaint.color = 0
        formatter.linePaint.color = 0
        formatter.vertexPaint.color =
            MaterialColors.getColor(binding.scatterPlot, com.google.android.material.R.attr.colorSecondary)
        formatter.vertexPaint.strokeWidth = PixelUtils.dpToPix(10f)

        val lastItemFormatter = LineAndPointFormatter()
        lastItemFormatter.isLegendIconEnabled = false
        lastItemFormatter.fillPaint.color = 0
        lastItemFormatter.linePaint.color = 0
        lastItemFormatter.vertexPaint.color =
            MaterialColors.getColor(binding.scatterPlot, R.attr.colorCustomColor1)
        lastItemFormatter.vertexPaint.strokeWidth = PixelUtils.dpToPix(15f)

        binding.scatterPlot
            .setDomainBoundaries(0, roundedMaximumDuration, BoundaryMode.FIXED)
        binding.scatterPlot
            .setRangeBoundaries(0, roundedMaximumDifficulty, BoundaryMode.FIXED)
        binding.scatterPlot.linesPerRangeLabel = 2
        binding.scatterPlot.rangeStepValue = 9.0
        binding.scatterPlot.linesPerDomainLabel = 2
        binding.scatterPlot.domainStepValue = 9.0

        binding.scatterPlot.setPlotMargins(0f, 0f, 0f, 0f)

        binding.scatterPlot.graph
            .getLineLabelStyle(XYGraphWidget.Edge.BOTTOM)
            .format =
            object : Format() {
                override fun format(
                    obj: Any,
                    toAppendTo: StringBuffer,
                    pos: FieldPosition?,
                ): StringBuffer {
                    val value = (obj as Number).toFloat().roundToInt()

                    return toAppendTo.append(Utils.displayableGameDuration(value.seconds))
                }

                override fun parseObject(
                    source: String?,
                    pos: ParsePosition?,
                ): Any? {
                    // unused
                    return null
                }
            }

        binding.scatterPlot.clear()
        binding.scatterPlot.addSeries(overallSeries, formatter)
        binding.scatterPlot.addSeries(lastItemSeries, lastItemFormatter)

        binding.scatterPlot.redraw()
    }
}
