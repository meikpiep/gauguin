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
import com.patrykandpatrick.vico.core.chart.decoration.ThresholdLine
import com.patrykandpatrick.vico.core.chart.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.shader.ColorShader
import com.patrykandpatrick.vico.core.component.text.textComponent
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
    private val statisticeManager: StatisticsManager by inject()

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
        val overall = statisticeManager.statistics().overall

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

        binding.startedstat.text = statisticeManager.totalStarted().toString()
        binding.hintedstat.text = statisticeManager.totalHinted().toString()
        binding.solvedstat.text = statisticeManager.totalSolved().toString() + " (" +
            String.format(
                "%.2f",
                solveRate(),
            ) + "%)"
        binding.solvedstreak.text = statisticeManager.currentStreak().toString()
        binding.longeststreak.text = statisticeManager.longestStreak().toString()
    }

    private fun fillCharts() {
        fillChart(
            difficultyDiagramFragment.binding.overallDifficulty,
            statisticeManager.statistics().overall.solvedDifficulty,
            statisticeManager.statistics().overall.solvedDifficulty.average(),
            com.google.android.material.R.attr.colorPrimary,
            com.google.android.material.R.attr.colorOnPrimary,
        )

        fillChart(
            durationDiagramFragment.binding.overallDuration,
            statisticeManager.statistics().overall.solvedDuration,
            statisticeManager.statistics().overall.solvedDuration.average(),
            com.google.android.material.R.attr.colorSecondary,
            com.google.android.material.R.attr.colorOnSecondary,
        )

        val streakSequence =
            statisticeManager.statistics().overall.streakSequence.map {
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

        val overall = statisticeManager.statistics().overall
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
                ThresholdLine(
                    thresholdValue = average.toFloat(),
                    thresholdLabel = getString(R.string.statistics_diagram_threshold_average_value),
                    lineComponent =
                        LineComponent(
                            color = MaterialColors.getColor(binding.root, R.attr.colorCustomColor1),
                        ),
                    labelComponent =
                        textComponent {
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
        return if (statisticeManager.totalStarted() == 0) {
            0.0
        } else {
            statisticeManager.totalSolved() * 100.0 / statisticeManager.totalStarted()
        }
    }

    private fun resetStatisticsDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_reset_statistics_title)
            .setMessage(R.string.dialog_reset_statistics_msg)
            .setNegativeButton(R.string.dialog_cancel) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .setPositiveButton(R.string.dialog_ok) { _: DialogInterface?, _: Int ->
                run {
                    statisticeManager.clearStatistics()
                    updateViews()
                }
            }
            .show()
    }
}
