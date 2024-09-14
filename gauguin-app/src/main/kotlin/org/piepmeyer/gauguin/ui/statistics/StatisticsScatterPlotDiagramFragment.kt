package org.piepmeyer.gauguin.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.androidplot.util.PixelUtils
import com.androidplot.xy.BoundaryMode
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYGraphWidget
import com.google.android.material.color.MaterialColors
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.Utils
import org.piepmeyer.gauguin.databinding.FragmentStatisticsScatterPlotDiagramBinding
import org.piepmeyer.gauguin.preferences.StatisticsManager
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import kotlin.math.nextUp
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

class StatisticsScatterPlotDiagramFragment :
    Fragment(R.layout.fragment_statistics_scatter_plot_diagram),
    KoinComponent {
    lateinit var binding: FragmentStatisticsScatterPlotDiagramBinding

    private val statisticsManager: StatisticsManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStatisticsScatterPlotDiagramBinding.inflate(inflater, parent, false)

        if (statisticsManager
                .statistics()
                .overall.solvedDuration
                .isNotEmpty()
        ) {
            createPlot()
        }

        return binding.root
    }

    private fun createPlot() {
        val series = SimpleXYSeries(null)

        statisticsManager
            .statistics()
            .overall.solvedDuration
            .withIndex()
            .associateWith { statisticsManager.statistics().overall.solvedDifficulty[it.index] }
            .forEach { (difficulty, duration) -> series.addLast(difficulty.value, duration) }

        val maximumDuration =
            statisticsManager
                .statistics()
                .overall.solvedDuration
                .max()
                .coerceAtLeast(60)
        val roundedMaximumDuration = ((maximumDuration * 1.2) / 60.0).nextUp().roundToInt() * 60

        val maximumDifficulty =
            statisticsManager
                .statistics()
                .overall.solvedDifficulty
                .max()
                .nextUp()
                .toInt()
                .coerceAtLeast(10)
        val roundedMaximumDifficulty = ((maximumDifficulty * 1.2) / 20.0).nextUp().roundToInt() * 20

        PixelUtils.init(context)

        val formatter = LineAndPointFormatter()
        formatter.isLegendIconEnabled = false
        formatter.fillPaint.color = 0
        formatter.linePaint.color = 0
        formatter.vertexPaint.color =
            MaterialColors.getColor(binding.scatterPlot, com.google.android.material.R.attr.colorSecondary)
        formatter.vertexPaint.strokeWidth = PixelUtils.dpToPix(10f)

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
                    val value = Math.round((obj as Number).toFloat())

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

        binding.scatterPlot.addSeries(series, formatter)
    }
}
