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
import org.piepmeyer.gauguin.difficulty.GameDifficultyRater
import org.piepmeyer.gauguin.difficulty.GameDifficultyRating
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.options.GameVariant
import java.math.BigDecimal
import java.text.DecimalFormat

class MainGameDifficultyLevelFragment(
    private val difficulty: DifficultySetting?,
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

        binding.veryEasyMaximumValue.text =
            formatDifficulty(
                uiRating.thresholdText(
                    DifficultySetting.EASY,
                ),
            )
        binding.easyMinimumValue.text = formatDifficulty(uiRating.thresholdText(DifficultySetting.EASY))
        binding.easyMaximumValue.text = formatDifficulty(uiRating.thresholdText(DifficultySetting.MEDIUM))
        binding.mediumMinimumValue.text = formatDifficulty(uiRating.thresholdText(DifficultySetting.MEDIUM))
        binding.mediumMaximumValue.text = formatDifficulty(uiRating.thresholdText(DifficultySetting.HARD))
        binding.hardMinimumValue.text = formatDifficulty(uiRating.thresholdText(DifficultySetting.HARD))
        binding.hardMaximumValue.text = formatDifficulty(uiRating.thresholdText(DifficultySetting.EXTREME))
        binding.extremeMinimumValue.text = formatDifficulty(uiRating.thresholdText(DifficultySetting.EXTREME))
    }

    private fun layoutWithDifficulty(
        difficulty: DifficultySetting,
        parent: ViewGroup?,
    ) {
        val referenceId =
            when (difficulty) {
                DifficultySetting.VERY_EASY -> R.id.veryEasy
                DifficultySetting.EASY -> R.id.easy
                DifficultySetting.MEDIUM -> R.id.medium
                DifficultySetting.HARD -> R.id.hard
                DifficultySetting.EXTREME -> R.id.extreme
            }

        setHighlighterConstraintsToMatch(difficulty, referenceId, parent)
    }

    private fun setHighlighterConstraintsToMatch(
        difficulty: DifficultySetting?,
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
            theme.resolveAttribute(com.google.android.material.R.attr.colorOnSecondaryContainer, typedValue, true)
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

    private fun hightlightedTextViews(difficulty: DifficultySetting): List<MaterialTextView> =
        when (difficulty) {
            DifficultySetting.VERY_EASY -> listOf(binding.veryEasy, binding.veryEasyMinimumValue, binding.veryEasyMaximumValue)
            DifficultySetting.EASY -> listOf(binding.easy, binding.easyMinimumValue, binding.easyMaximumValue)
            DifficultySetting.MEDIUM -> listOf(binding.medium, binding.mediumMinimumValue, binding.mediumMaximumValue)
            DifficultySetting.HARD -> listOf(binding.hard, binding.hardMinimumValue, binding.hardMaximumValue)
            DifficultySetting.EXTREME -> listOf(binding.extreme, binding.extremeMinimumValue, binding.extremeMaximumValue)
        }

    private fun hightlightedImageViews(difficulty: DifficultySetting): List<ImageView> =
        when (difficulty) {
            DifficultySetting.VERY_EASY ->
                listOf(
                    binding.ratingStarVeryEasyOne,
                    binding.ratingStarVeryEasyTwo,
                    binding.ratingStarVeryEasyThree,
                    binding.ratingStarVeryEasyFour,
                )
            DifficultySetting.EASY ->
                listOf(
                    binding.ratingStarEasyOne,
                    binding.ratingStarEasyTwo,
                    binding.ratingStarEasyThree,
                    binding.ratingStarEasyFour,
                )
            DifficultySetting.MEDIUM ->
                listOf(
                    binding.ratingStarMediumOne,
                    binding.ratingStarMediumTwo,
                    binding.ratingStarMediumThree,
                    binding.ratingStarMediumFour,
                )
            DifficultySetting.HARD ->
                listOf(
                    binding.ratingStarHardOne,
                    binding.ratingStarHardTwo,
                    binding.ratingStarHardThree,
                    binding.ratingStarHardFour,
                )
            DifficultySetting.EXTREME ->
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
