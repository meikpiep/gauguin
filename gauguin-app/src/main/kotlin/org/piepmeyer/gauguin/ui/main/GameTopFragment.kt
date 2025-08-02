package org.piepmeyer.gauguin.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.Utils
import org.piepmeyer.gauguin.databinding.FragmentMainGameTopBinding
import org.piepmeyer.gauguin.difficulty.DisplayableGameDifficulty
import org.piepmeyer.gauguin.difficulty.GameDifficultyRater
import org.piepmeyer.gauguin.difficulty.ensureDifficultyCalculated
import org.piepmeyer.gauguin.difficulty.human.HumanDifficultyCalculator
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.game.PlayTimeListener
import org.piepmeyer.gauguin.options.DifficultySetting
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.ui.difficulty.MainGameDifficultyLevelBalloon
import org.piepmeyer.gauguin.ui.difficulty.MainGameDifficultyLevelFragment

class GameTopFragment :
    Fragment(R.layout.fragment_main_game_top),
    PlayTimeListener,
    KoinComponent {
    private val game: Game by inject()
    private val gameLifecycle: GameLifecycle by inject()
    private val applicationPreferences: ApplicationPreferences by inject()

    private lateinit var binding: FragmentMainGameTopBinding
    var tinyMode = false

    private val showtimer = applicationPreferences.showTimer()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainGameTopBinding.inflate(inflater, parent, false)

        val onClickListener =
            View.OnClickListener {
                val difficulty = GameDifficultyRater().difficulty(game.grid)

                MainGameDifficultyLevelBalloon(difficulty, game.grid.variant).showBalloon(
                    baseView = it,
                    inflater = inflater,
                    parent = parent!!,
                    lifecycleOwner = this,
                    anchorView =
                        if (binding.ratingStarThree.isVisible) {
                            binding.ratingStarThree
                        } else {
                            binding.difficulty
                        },
                )
            }

        if (tinyMode) {
            binding.appname.visibility = View.GONE
        }

        binding.difficulty.setOnClickListener(onClickListener)
        binding.ratingStarOne.setOnClickListener(onClickListener)
        binding.ratingStarTwo.setOnClickListener(onClickListener)
        binding.ratingStarThree.setOnClickListener(onClickListener)
        binding.ratingStarFour.setOnClickListener(onClickListener)

        return binding.root
    }

    override fun onPause() {
        gameLifecycle.removePlayTimeListener(this)

        super.onPause()
    }

    override fun onResume() {
        gameLifecycle.addPlayTimeListener(this)

        super.onResume()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        val viewModel: MainViewModel by viewModels()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    if (it.state == MainUiState.PLAYING || it.state == MainUiState.ALREADY_SOLVED) {
                        freshGridWasCreated()
                    }

                    if (it.state == MainUiState.CALCULATING_NEW_GRID) {
                        if (!tinyMode) {
                            binding.difficulty.visibility = View.INVISIBLE
                            binding.playtime.visibility = View.INVISIBLE
                            binding.ratingStarOne.visibility = View.INVISIBLE
                            binding.ratingStarTwo.visibility = View.INVISIBLE
                            binding.ratingStarThree.visibility = View.INVISIBLE
                            binding.ratingStarFour.visibility = View.INVISIBLE
                        }
                    } else {
                        updateTimerVisibility(it.state)
                    }
                }
            }
        }
    }

    private fun updateTimerVisibility(state: MainUiState) {
        binding.playtime.visibility =
            if (showtimer || state in listOf(MainUiState.SOLVED, MainUiState.ALREADY_SOLVED)) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
    }

    private fun freshGridWasCreated() {
        requireActivity().runOnUiThread {
            val rater = GameDifficultyRater()
            val rating = rater.byVariant(game.grid.variant)
            val difficultyType = rater.difficulty(game.grid)

            game.grid.ensureDifficultyCalculated()
            val classicalDifficulty = game.grid.difficulty.classicalRating!!

            binding.difficulty.text =
                MainGameDifficultyLevelFragment.formatDifficulty(
                    DisplayableGameDifficulty(rating).displayableDifficultyValue(classicalDifficulty),
                )

            setStarsByDifficulty(difficultyType)

            val visibilityOfStars =
                if (tinyMode || rating == null) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

            binding.ratingStarOne.visibility = visibilityOfStars
            binding.ratingStarTwo.visibility = visibilityOfStars
            binding.ratingStarThree.visibility = visibilityOfStars
            binding.ratingStarFour.visibility = visibilityOfStars

            binding.difficulty.visibility = View.VISIBLE
            binding.playtime.text = Utils.displayableGameDuration(game.grid.playTime)
        }

        if (resources.getBoolean(R.bool.debuggable)) {
            lifecycleScope.launch(Dispatchers.Default) {
                HumanDifficultyCalculator(game.grid).ensureDifficultyCalculated()

                val text = binding.difficulty.text as String + " (${game.grid.difficulty.humanDifficultyDisplayable()})"

                launch(Dispatchers.Main) {
                    if (!binding.difficulty.text.contains(' ')) {
                        binding.difficulty.text = text
                    }
                }
            }
        }
    }

    private fun setStarsByDifficulty(difficulty: DifficultySetting?) {
        if (difficulty == null) return

        setStarByDifficulty(
            binding.ratingStarOne,
            difficulty,
            DifficultySetting.EASY,
        )
        setStarByDifficulty(
            binding.ratingStarTwo,
            difficulty,
            DifficultySetting.MEDIUM,
        )
        setStarByDifficulty(
            binding.ratingStarThree,
            difficulty,
            DifficultySetting.HARD,
        )
        setStarByDifficulty(
            binding.ratingStarFour,
            difficulty,
            DifficultySetting.EXTREME,
        )
    }

    private fun setStarByDifficulty(
        view: ImageView,
        difficulty: DifficultySetting,
        minimumDifficulty: DifficultySetting,
    ) {
        if (difficulty >= minimumDifficulty) {
            view.setImageResource(R.drawable.filled_star_20)
        } else {
            view.setImageResource(R.drawable.outline_star_20)
        }
    }

    override fun playTimeUpdated() {
        activity?.runOnUiThread {
            if (this::binding.isInitialized) {
                binding.playtime.text = Utils.displayableGameDuration(game.grid.playTime)
            }
        }
    }
}
