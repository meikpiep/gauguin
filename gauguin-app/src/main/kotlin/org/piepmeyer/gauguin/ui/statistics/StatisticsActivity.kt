package org.piepmeyer.gauguin.ui.statistics

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.decoration.HorizontalLine
import com.patrykandpatrick.vico.core.chart.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.shader.ColorShader
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.model.CartesianChartModel
import com.patrykandpatrick.vico.core.model.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.model.LineCartesianLayerModel
import com.patrykandpatrick.vico.views.chart.CartesianChartView
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.Utils
import org.piepmeyer.gauguin.databinding.ActivityStatisticsBinding
import org.piepmeyer.gauguin.preferences.StatisticsManager
import org.piepmeyer.gauguin.ui.ActivityUtils
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

class StatisticsActivity : AppCompatActivity() {
    private val activityUtils: ActivityUtils by inject()
    private val statisticsManager: StatisticsManager by inject()

    private lateinit var binding: ActivityStatisticsBinding
    private lateinit var difficultyDiagramFragment: StatisticsDifficultyDiagramFragment
    private lateinit var durationDiagramFragment: StatisticsDurationDiagramFragment
    private lateinit var streaksDiagramFragment: StatisticsStreaksDiagramFragment

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clearstats.setOnClickListener { _: View? ->
            resetStatisticsDialog()
        }

        activityUtils.configureFullscreen(this)

        difficultyDiagramFragment = StatisticsDifficultyDiagramFragment()
        durationDiagramFragment = StatisticsDurationDiagramFragment()
        streaksDiagramFragment = StatisticsStreaksDiagramFragment()

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.overallDifficultyCardView, difficultyDiagramFragment)
        ft.replace(R.id.overallDurationCardView, durationDiagramFragment)
        ft.replace(R.id.overallStreaksCardView, streaksDiagramFragment)
        ft.commit()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        updateViews()
    }

    private fun updateViews() {
        val overall = statisticsManager.statistics().overall

        val chartsAvailable =
            overall.solvedDifficulty.isNotEmpty() &&
                overall.solvedDuration.isNotEmpty()

        if (chartsAvailable) {
            fillCharts()
            binding.noStatisticsAvailableYetCardView.visibility = View.INVISIBLE
        } else {
            hideCharts()
            binding.noStatisticsAvailableYetCardView.visibility = View.VISIBLE
        }

        binding.startedstat.text = statisticsManager.totalStarted().toString()
        binding.hintedstat.text = statisticsManager.totalHinted().toString()
        binding.solvedstat.text = statisticsManager.totalSolved().toString() + " (" +
            String.format(
                "%.2f",
                solveRate(),
            ) + "%)"
        binding.solvedstreak.text = statisticsManager.currentStreak().toString()
        binding.longeststreak.text = statisticsManager.longestStreak().toString()
    }

    private fun fillCharts() {
        fillChart(
            difficultyDiagramFragment.binding.overallDifficulty,
            statisticsManager.statistics().overall.solvedDifficulty,
            statisticsManager.statistics().overall.solvedDifficulty.average(),
            com.google.android.material.R.attr.colorPrimary,
            com.google.android.material.R.attr.colorOnPrimary,
        )

        fillChart(
            durationDiagramFragment.binding.overallDuration,
            statisticsManager.statistics().overall.solvedDuration,
            statisticsManager.statistics().overall.solvedDuration.average(),
            com.google.android.material.R.attr.colorSecondary,
            com.google.android.material.R.attr.colorOnSecondary,
        )

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

        streaksDiagramFragment.binding.overallStreaks.setModel(
            CartesianChartModel(
                ColumnCartesianLayerModel.build {
                    series(filledUpStreakSequence)
                },
            ),
        )

        val verticalDurationAxis =
            durationDiagramFragment.binding.overallDuration
                .chart!!.startAxis!! as VerticalAxis<AxisPosition.Vertical.Start>

        verticalDurationAxis.valueFormatter =
            AxisValueFormatter { value, _, _ ->
                Utils.displayableGameDuration(value.toInt().seconds)
            }

        val overall = statisticsManager.statistics().overall
        val difficultyAverage = overall.solvedDifficultySum / overall.gamesSolved
        val durationAverage = overall.solvedDurationSum / overall.gamesSolved

        difficultyDiagramFragment.binding.overallDifficultyMinimum.text = overall.solvedDifficultyMinimum.roundToInt().toString()
        difficultyDiagramFragment.binding.overallDifficultyAverage.text = difficultyAverage.roundToInt().toString()
        difficultyDiagramFragment.binding.overallDifficultyMaximum.text = overall.solvedDifficultyMaximum.roundToInt().toString()

        durationDiagramFragment.binding.overallDurationMinimum.text =
            Utils.displayableGameDuration(overall.solvedDurationMinimum.seconds)
        durationDiagramFragment.binding.overallDurationAverage.text = Utils.displayableGameDuration(durationAverage.seconds)
        durationDiagramFragment.binding.overallDurationMaximum.text =
            Utils.displayableGameDuration(overall.solvedDurationMaximum.seconds)
    }

    private fun <T : Number> fillChart(
        chartView: CartesianChartView,
        chartData: List<T>,
        average: Double,
        lineColor: Int,
        areaColor: Int,
    ) {
        val wrappedChartData = dublicateIfSingleItem(chartData)

        chartView.setModel(
            CartesianChartModel(
                LineCartesianLayerModel.build {
                    series(wrappedChartData)
                },
            ),
        )

        if (wrappedChartData.any { it.toDouble() != average }) {
            chartView.chart!!.addDecoration(
                HorizontalLine(
                    y = { _ -> average.toFloat() },
                    label = { _ -> getString(R.string.statistics_diagram_threshold_average_value) },
                    line =
                        LineComponent(
                            color = MaterialColors.getColor(binding.root, R.attr.colorCustomColor1),
                            thicknessDp = 2f,
                        ),
                    labelComponent =
                        TextComponent.build {
                            color = MaterialColors.getColor(binding.root, R.attr.colorCustomColor1)
                        },
                ),
            )
        }

        addColorToLine(
            chartView,
            lineColor,
            areaColor,
        )
    }

    private fun <T> dublicateIfSingleItem(items: List<T>): List<T> {
        return if (items.size == 1) {
            listOf(
                items.first(),
                items.first(),
            )
        } else {
            items
        }
    }

    private fun hideCharts() {
        binding.overallDifficultyCardView.visibility = View.GONE
        binding.overallDurationCardView.visibility = View.GONE
        binding.overallStreaksCardView.visibility = View.GONE
    }

    private fun addColorToLine(
        chartView: CartesianChartView,
        foregroundColor: Int,
        backgroundColor: Int,
    ) {
        val lineLayer = chartView.chart!!.layers.first() as LineCartesianLayer
        val line = lineLayer.lines.first()

        line.shader =
            ColorShader(
                MaterialColors.getColor(binding.root, foregroundColor),
            )
        line.backgroundShader =
            ColorShader(
                MaterialColors.compositeARGBWithAlpha(
                    MaterialColors.getColor(binding.root, backgroundColor),
                    128,
                ),
            )
    }

    private fun solveRate(): Double {
        return if (statisticsManager.totalStarted() == 0) {
            0.0
        } else {
            statisticsManager.totalSolved() * 100.0 / statisticsManager.totalStarted()
        }
    }

    private fun resetStatisticsDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.statistics_dialog_reset_statistics_title)
            .setMessage(R.string.statistics_dialog_reset_statistics_message)
            .setNegativeButton(
                R.string.statistics_dialog_reset_statistics_cancel_button,
            ) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .setPositiveButton(R.string.statistics_dialog_reset_statistics_ok_button) { _: DialogInterface?, _: Int ->
                run {
                    statisticsManager.clearStatistics()
                    updateViews()
                }
            }
            .show()
    }
}
