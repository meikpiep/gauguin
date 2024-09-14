package org.piepmeyer.gauguin.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.FragmentStatisticsMultiDiagramBinding

class StatisticsMultiDiagramFragment() : Fragment(R.layout.fragment_statistics_multi_diagram) {
    lateinit var binding: FragmentStatisticsMultiDiagramBinding

    private var scatterPlotDiagramFragment: StatisticsScatterPlotDiagramFragment? = null
    private var durationDiagramFragment: StatisticsDurationDiagramFragment? = null

    constructor(
        scatterPlotDiagramFragment: StatisticsScatterPlotDiagramFragment,
        durationDiagramFragment: StatisticsDurationDiagramFragment,
    ) : this() {
        this.scatterPlotDiagramFragment = scatterPlotDiagramFragment
        this.durationDiagramFragment = durationDiagramFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStatisticsMultiDiagramBinding.inflate(inflater, parent, false)

        val ft = parentFragmentManager.beginTransaction()
        scatterPlotDiagramFragment?.let {
            ft.replace(binding.multiDiagramFrameScatterPlot.id, it)
        }
        durationDiagramFragment?.let {
            ft.replace(binding.multiDiagramFrameDurationPlot.id, it)
        }
        ft.commit()

        binding.toggleGroupMultiDiagram.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                binding.multiDiagramFrameScatterPlot.visibility =
                    if (checkedId == binding.toggleGroupButtonScatterPlot.id) View.VISIBLE else View.GONE
                binding.multiDiagramFrameDurationPlot.visibility =
                    if (checkedId == binding.toggleGroupButtonDuration.id) View.VISIBLE else View.GONE
            }
        }

        binding.toggleGroupMultiDiagram.check(binding.toggleGroupButtonScatterPlot.id)

        return binding.root
    }
}
