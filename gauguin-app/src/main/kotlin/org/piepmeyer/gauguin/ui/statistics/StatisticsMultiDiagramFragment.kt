package org.piepmeyer.gauguin.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.FragmentLegacyStatisticsMultiDiagramBinding

class StatisticsMultiDiagramFragment() : Fragment(R.layout.fragment_statistics_multi_diagram) {
    lateinit var binding: FragmentLegacyStatisticsMultiDiagramBinding

    private var scatterPlotDiagramFragment: StatisticsScatterPlotDiagramFragment? = null
    private var durationDiagramFragment: StatisticsDurationDiagramFragment? = null

    private val viewModel: StatisticsViewModel by activityViewModel()

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
        binding = FragmentLegacyStatisticsMultiDiagramBinding.inflate(inflater, parent, false)

        parentFragmentManager.commit {
            scatterPlotDiagramFragment?.let {
                replace(binding.multiDiagramFrameScatterPlot.id, it)
            }
            durationDiagramFragment?.let {
                replace(binding.multiDiagramFrameDurationPlot.id, it)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.historyState.collect {
                    when (it) {
                        is HistoryState.HistoryLoaded -> {
                            binding.root.visibility = View.VISIBLE
                        }

                        else -> {
                            binding.root.visibility = View.GONE
                        }
                    }
                }
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
