package org.piepmeyer.gauguin.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.FragmentMainFastFinishingModeBinding
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameModeListener

class FastFinishingModeFragment : Fragment(R.layout.fragment_main_fast_finishing_mode), KoinComponent,
    GameModeListener {
    private val game: Game by inject()

    private lateinit var binding: FragmentMainFastFinishingModeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainFastFinishingModeBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.exitFastFinishingMode.setOnClickListener{ game.exitFastFinishingMode() }

        game.addGameModeListener(this)
    }

    override fun changedGameMode() {
        binding.fastFinishModeCardView.visibility = if (game.isInFastFinishingMode()) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }
}