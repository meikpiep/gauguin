package org.piepmeyer.gauguin.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.FragmentMainFastFinishingModeBinding
import org.piepmeyer.gauguin.game.FastFinishingModeState
import org.piepmeyer.gauguin.game.Game

class FastFinishingModeFragment :
    Fragment(R.layout.fragment_main_fast_finishing_mode),
    KoinComponent {
    private val game: Game by inject()
    private val viewModel: MainViewModel by inject()

    private lateinit var binding: FragmentMainFastFinishingModeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainFastFinishingModeBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.exitFastFinishingMode.setOnClickListener { game.exitFastFinishingMode() }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fastFinishingModeState.collect {
                    binding.fastFinishModeCardView.visibility =
                        if (it == FastFinishingModeState.Fast) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                }
            }
        }
    }
}
