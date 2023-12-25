package org.piepmeyer.gauguin.ui.main

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.FragmentMainGameDifficultyLevelBinding
import org.piepmeyer.gauguin.difficulty.GameDifficulty
import org.piepmeyer.gauguin.difficulty.GameDifficultyRater
import org.piepmeyer.gauguin.game.Game

class MainGameDifficultyLevelFragment : Fragment(R.layout.fragment_main_game_difficulty_level),
    KoinComponent {
    private val game: Game by inject()

    private lateinit var binding: FragmentMainGameDifficultyLevelBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainGameDifficultyLevelBinding.inflate(inflater, parent, false)

        val rater = GameDifficultyRater()

        val difficulty = rater.difficulty(game.grid)
        val rating = rater.byVariant(game.grid.variant)

        rating?.let {
            binding.veryEasyMaximumValue.text = it.thresholdEasy.toInt().toString()
            binding.easyMinimumValue.text = it.thresholdEasy.toInt().toString()
            binding.easyMaximumValue.text = it.thresholdMedium.toInt().toString()
            binding.mediumMinimumValue.text = it.thresholdMedium.toInt().toString()
            binding.mediumMaximumValue.text = it.thresholdHard.toInt().toString()
            binding.hardMinimumValue.text = it.thresholdHard.toInt().toString()
            binding.hardMaximumValue.text = it.thresholdExtreme.toInt().toString()
            binding.extremeMinimumValue.text = it.thresholdExtreme.toInt().toString()
        }

        val referenceId = when (difficulty) {
            GameDifficulty.VERY_EASY -> R.id.veryEasy
            GameDifficulty.EASY -> R.id.easy
            GameDifficulty.MEDIUM -> R.id.medium
            GameDifficulty.HARD -> R.id.hard
            GameDifficulty.EXTREME -> R.id.extreme
        }

        val constraintSet = ConstraintSet()

        val margin = (4 * parent!!.resources.displayMetrics.density).toInt()

        constraintSet.clone(binding.mainGameDifficultyLevelConstaintLayout)
        constraintSet.connect(R.id.difficultyLevelHighlighter, ConstraintSet.TOP, referenceId, ConstraintSet.TOP, -margin)
        constraintSet.connect(R.id.difficultyLevelHighlighter, ConstraintSet.BOTTOM, referenceId, ConstraintSet.BOTTOM, -margin)

        TransitionManager.beginDelayedTransition(binding.mainGameDifficultyLevelConstaintLayout)
        constraintSet.applyTo(binding.mainGameDifficultyLevelConstaintLayout)

        return binding.root
    }
}
