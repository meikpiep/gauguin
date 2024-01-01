package org.piepmeyer.gauguin.ui.difficulty

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.FragmentGameDifficultyLevelBinding
import org.piepmeyer.gauguin.difficulty.DisplayableGameDifficultyThreshold
import org.piepmeyer.gauguin.difficulty.GameDifficulty
import org.piepmeyer.gauguin.difficulty.GameDifficultyRater
import org.piepmeyer.gauguin.difficulty.GameDifficultyRating
import org.piepmeyer.gauguin.options.GameVariant
import java.math.BigDecimal
import java.text.DecimalFormat

class MainGameDifficultyLevelFragment(
    private val difficulty: GameDifficulty?,
    private val variant: GameVariant,
) : Fragment(R.layout.fragment_game_difficulty_level) {

    private lateinit var binding: FragmentGameDifficultyLevelBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameDifficultyLevelBinding.inflate(inflater, parent, false)

        val rating = GameDifficultyRater().byVariant(variant)

        if (rating != null) {
            layoutWithRating(rating)

            if (difficulty != null) {
                layoutWithDifficulty(difficulty, parent)
            } else {
                layoutWithoutDifficulty()
            }
        } else {
            layoutWithoutRating(parent)
        }

        return binding.root
    }

    private fun layoutWithoutRating(parent: ViewGroup?) {
        binding.root.allViews
            .minus(
                setOf(
                    binding.difficultyLevelHighlighter,
                    binding.noDifficultyCalculated,
                    binding.mainGameDifficultyLevelConstaintLayout
                )
            )
            .forEach { it.visibility = View.GONE }

        binding.noDifficultyCalculated.visibility = View.VISIBLE

        setHighlighterConstraintsToMatch(R.id.noDifficultyCalculated, parent)
    }

    private fun layoutWithRating(rating: GameDifficultyRating) {
        val uiRating = DisplayableGameDifficultyThreshold(rating)

        binding.veryEasyMaximumValue.text = formatDifficulty(uiRating.thresholdText(GameDifficulty.EASY))
        binding.easyMinimumValue.text = formatDifficulty(uiRating.thresholdText(GameDifficulty.EASY))
        binding.easyMaximumValue.text = formatDifficulty(uiRating.thresholdText(GameDifficulty.MEDIUM))
        binding.mediumMinimumValue.text = formatDifficulty(uiRating.thresholdText(GameDifficulty.MEDIUM))
        binding.mediumMaximumValue.text = formatDifficulty(uiRating.thresholdText(GameDifficulty.HARD))
        binding.hardMinimumValue.text = formatDifficulty(uiRating.thresholdText(GameDifficulty.HARD))
        binding.hardMaximumValue.text = formatDifficulty(uiRating.thresholdText(GameDifficulty.EXTREME))
        binding.extremeMinimumValue.text = formatDifficulty(uiRating.thresholdText(GameDifficulty.EXTREME))
    }

    private fun layoutWithDifficulty(
        difficulty: GameDifficulty,
        parent: ViewGroup?
    ) {
        val referenceId = when (difficulty) {
            GameDifficulty.VERY_EASY -> R.id.veryEasy
            GameDifficulty.EASY -> R.id.easy
            GameDifficulty.MEDIUM -> R.id.medium
            GameDifficulty.HARD -> R.id.hard
            GameDifficulty.EXTREME -> R.id.extreme
        }

        setHighlighterConstraintsToMatch(referenceId, parent)
    }

    private fun setHighlighterConstraintsToMatch(referenceId: Int, parent: ViewGroup?) {
        val constraintSet = ConstraintSet()

        val margin = (4 * parent!!.resources.displayMetrics.density).toInt()

        constraintSet.clone(binding.mainGameDifficultyLevelConstaintLayout)
        constraintSet.connect(
            R.id.difficultyLevelHighlighter,
            ConstraintSet.TOP,
            referenceId,
            ConstraintSet.TOP,
            -margin
        )
        constraintSet.connect(
            R.id.difficultyLevelHighlighter,
            ConstraintSet.BOTTOM,
            referenceId,
            ConstraintSet.BOTTOM,
            -margin
        )

        TransitionManager.beginDelayedTransition(binding.mainGameDifficultyLevelConstaintLayout)
        constraintSet.applyTo(binding.mainGameDifficultyLevelConstaintLayout)
    }

    private fun layoutWithoutDifficulty() {
        binding.difficultyLevelHighlighter.visibility = View.INVISIBLE
    }

    companion object {
        fun formatDifficulty(threshold: BigDecimal): String {
            return DecimalFormat("###0.#").format(threshold)
        }
    }
}
