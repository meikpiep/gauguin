package org.piepmeyer.gauguin.ui.statistics.legacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.FragmentLegacyStatisticsMultiDiagramBinding

class LegacyStatisticsMultiDiagramFragment() : Fragment(R.layout.fragment_legacy_statistics_multi_diagram) {
    lateinit var binding: FragmentLegacyStatisticsMultiDiagramBinding

    private var scatterPlotDiagramFragment: LegacyStatisticsScatterPlotDiagramFragment? = null
    private var durationDiagramFragment: LegacyStatisticsDurationDiagramFragment? = null

    constructor(
        scatterPlotDiagramFragment: LegacyStatisticsScatterPlotDiagramFragment,
        durationDiagramFragment: LegacyStatisticsDurationDiagramFragment,
    ) : this() {
        this.scatterPlotDiagramFragment = scatterPlotDiagramFragment
        this.durationDiagramFragment = durationDiagramFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLegacyStatisticsMultiDiagramBinding.inflate(inflater, parent, false)

        parentFragmentManager.commit {
            scatterPlotDiagramFragment?.let {
                replace(binding.multiDiagramFrameScatterPlot.id, it)
            }
            durationDiagramFragment?.let {
                replace(binding.multiDiagramFrameDurationPlot.id, it)
            }
        }

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
