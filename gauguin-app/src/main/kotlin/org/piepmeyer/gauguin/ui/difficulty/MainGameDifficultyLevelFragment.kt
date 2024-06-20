package org.piepmeyer.gauguin.ui.difficulty

import android.content.res.ColorStateList
import android.os.Bundle
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import com.google.android.material.textview.MaterialTextView
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
        savedInstanceState: Bundle?,
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
                    binding.mainGameDifficultyLevelConstaintLayout,
                ),
            ).forEach { it.visibility = View.GONE }

        binding.noDifficultyCalculated.visibility = View.VISIBLE

        setHighlighterConstraintsToMatch(difficulty, R.id.noDifficultyCalculated, parent)
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
        parent: ViewGroup?,
    ) {
        val referenceId =
            when (difficulty) {
                GameDifficulty.VERY_EASY -> R.id.veryEasy
                GameDifficulty.EASY -> R.id.easy
                GameDifficulty.MEDIUM -> R.id.medium
                GameDifficulty.HARD -> R.id.hard
                GameDifficulty.EXTREME -> R.id.extreme
            }

        setHighlighterConstraintsToMatch(difficulty, referenceId, parent)
    }

    private fun setHighlighterConstraintsToMatch(
        difficulty: GameDifficulty?,
        referenceId: Int,
        parent: ViewGroup?,
    ) {
        val constraintSet = ConstraintSet()

        val margin = (4 * parent!!.resources.displayMetrics.density).toInt()

        constraintSet.clone(binding.mainGameDifficultyLevelConstaintLayout)
        constraintSet.connect(
            R.id.difficultyLevelHighlighter,
            ConstraintSet.TOP,
            referenceId,
            ConstraintSet.TOP,
            -margin,
        )
        constraintSet.connect(
            R.id.difficultyLevelHighlighter,
            ConstraintSet.BOTTOM,
            referenceId,
            ConstraintSet.BOTTOM,
            -margin,
        )

        if (difficulty != null) {
            val typedValue = TypedValue()
            val theme = binding.hard.context.theme
            theme.resolveAttribute(R.attr.colorMainTopPanelForeground, typedValue, true)
            @ColorInt val color = typedValue.data

            hightlightedTextViews(difficulty).forEach {
                it.setTextColor(color)
            }

            hightlightedImageViews(difficulty).forEach {
                it.imageTintList = ColorStateList.valueOf(color)
            }
        }

        TransitionManager.beginDelayedTransition(binding.mainGameDifficultyLevelConstaintLayout)
        constraintSet.applyTo(binding.mainGameDifficultyLevelConstaintLayout)
    }

    private fun hightlightedTextViews(difficulty: GameDifficulty): List<MaterialTextView> =
        when (difficulty) {
            GameDifficulty.VERY_EASY -> listOf(binding.veryEasy, binding.veryEasyMinimumValue, binding.veryEasyMaximumValue)
            GameDifficulty.EASY -> listOf(binding.easy, binding.easyMinimumValue, binding.easyMaximumValue)
            GameDifficulty.MEDIUM -> listOf(binding.medium, binding.mediumMinimumValue, binding.mediumMaximumValue)
            GameDifficulty.HARD -> listOf(binding.hard, binding.hardMinimumValue, binding.hardMaximumValue)
            GameDifficulty.EXTREME -> listOf(binding.extreme, binding.extremeMinimumValue, binding.extremeMaximumValue)
        }

    private fun hightlightedImageViews(difficulty: GameDifficulty): List<ImageView> =
        when (difficulty) {
            GameDifficulty.VERY_EASY ->
                listOf(
                    binding.ratingStarVeryEasyOne,
                    binding.ratingStarVeryEasyTwo,
                    binding.ratingStarVeryEasyThree,
                    binding.ratingStarVeryEasyFour,
                )
            GameDifficulty.EASY ->
                listOf(
                    binding.ratingStarEasyOne,
                    binding.ratingStarEasyTwo,
                    binding.ratingStarEasyThree,
                    binding.ratingStarEasyFour,
                )
            GameDifficulty.MEDIUM ->
                listOf(
                    binding.ratingStarMediumOne,
                    binding.ratingStarMediumTwo,
                    binding.ratingStarMediumThree,
                    binding.ratingStarMediumFour,
                )
            GameDifficulty.HARD ->
                listOf(
                    binding.ratingStarHardOne,
                    binding.ratingStarHardTwo,
                    binding.ratingStarHardThree,
                    binding.ratingStarHardFour,
                )
            GameDifficulty.EXTREME ->
                listOf(
                    binding.ratingStarExtremeOne,
                    binding.ratingStarExtremeTwo,
                    binding.ratingStarExtremeThree,
                    binding.ratingStarExtremeFour,
                )
        }

    private fun layoutWithoutDifficulty() {
        binding.difficultyLevelHighlighter.visibility = View.INVISIBLE
    }

    companion object {
        private val formatScaleZero = DecimalFormat("###0.#")
        private val formatScaleOne = DecimalFormat("###0.0")

        fun formatDifficulty(threshold: BigDecimal): String =
            if (threshold.scale() == 1) {
                formatScaleOne.format(threshold)
            } else {
                formatScaleZero.format(threshold)
            }
    }
}
