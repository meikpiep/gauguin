package org.piepmeyer.gauguin.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.core.component.KoinComponent
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.FragmentStatisticsDurationDiagramBinding

class StatisticsDurationDiagramFragment :
    Fragment(R.layout.fragment_statistics_duration_diagram),
    KoinComponent {
    lateinit var binding: FragmentStatisticsDurationDiagramBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStatisticsDurationDiagramBinding.inflate(inflater, parent, false)
        return binding.root
    }
}
