package org.piepmeyer.gauguin.ui.statistics

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.databinding.ActivityStatisticsMaximizeOneDiagramBinding
import org.piepmeyer.gauguin.ui.ActivityUtils

class StatisticsMaximizeOneDiagramActivity : AppCompatActivity() {
    private val activityUtils: ActivityUtils by inject()

    private lateinit var binding: ActivityStatisticsMaximizeOneDiagramBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityUtils.configureTheme(this)
        binding = ActivityStatisticsMaximizeOneDiagramBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        activityUtils.configureMainContainerBackground(binding.root)
        activityUtils.configureRootView(binding.root)

        binding.statisticsClose.setOnClickListener {
            finishAfterTransition()
        }

        activityUtils.configureFullscreen(this)

        val diagramType = DiagramType.valueOf(requireNotNull(intent.getStringExtra(EXTRA_KEY_DIAGRAM_TYPE)))

        val diagramFragment =
            when (diagramType) {
                DiagramType.DURATION -> StatisticsDurationDiagramFragment()
                DiagramType.STREAKS -> StatisticsStreaksDiagramFragment()
                DiagramType.SCATTER_PLOT -> StatisticsScatterPlotDiagramFragment()
                else -> StatisticsDifficultyDiagramFragment()
            }

        supportFragmentManager.commit {
            replace(binding.diagramCardView.id, diagramFragment)
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.root,
        ) { v, insets ->
            val innerPadding =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout(),
                )
            v.setPadding(
                0,
                0,
                0,
                innerPadding.bottom,
            )

            WindowInsetsCompat.CONSUMED
        }
    }

    enum class DiagramType {
        DIFFICULTY,
        STREAKS,
        SCATTER_PLOT,
        DURATION,
    }

    companion object {
        const val EXTRA_KEY_DIAGRAM_TYPE: String = "org.piepmeyer.gauguin.ui.statistics.diagramType"
    }
}
