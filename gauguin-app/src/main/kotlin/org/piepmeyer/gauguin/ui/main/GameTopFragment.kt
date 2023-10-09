package org.piepmeyer.gauguin.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.Utils
import org.piepmeyer.gauguin.creation.GridDifficultyCalculator
import org.piepmeyer.gauguin.databinding.FragmentMainGameTopBinding
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.game.GridCreationListener
import org.piepmeyer.gauguin.game.PlayTimeListener
import org.piepmeyer.gauguin.options.ApplicationPreferencesImpl
import org.piepmeyer.gauguin.options.GameDifficulty
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration

class GameTopFragment : Fragment(R.layout.fragment_main_game_top), GridCreationListener,
    PlayTimeListener, KoinComponent {
    private val game: Game by inject()
    private val gameLifecycle: GameLifecycle by inject()
    private val applicationPreferences: ApplicationPreferencesImpl by inject()

    private lateinit var binding: FragmentMainGameTopBinding

    private var timeDescription: String? = null
    private var showtimer = false

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainGameTopBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onPause() {
        gameLifecycle.removePlayTimeListener(this)

        super.onPause()
    }

    override fun onResume() {
        this.showtimer = applicationPreferences.preferences.getBoolean("showtimer", true)
        updateTimerVisibility()

        gameLifecycle.addPlayTimeListener(this)

        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        game.addGridCreationListener(this)

        freshGridWasCreated()

        updateTimerVisibility()
    }

    private fun updateTimerVisibility() {
        binding.playtime.visibility = if (showtimer) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    override fun freshGridWasCreated() {
        if (!isAdded) {
            return
        }
        requireActivity().runOnUiThread {
            val difficultyCalculator = GridDifficultyCalculator(game.grid)
            binding.difficulty.text = difficultyCalculator.info()
            setStarsByDifficulty(difficultyCalculator)

            timeDescription?.let { binding.playtime.text = it }
        }
    }

    private fun setStarsByDifficulty(difficultyCalculator: GridDifficultyCalculator) {
        setStarByDifficulty(
            binding.ratingStarOne,
            difficultyCalculator.difficulty,
            GameDifficulty.EASY
        )
        setStarByDifficulty(
            binding.ratingStarTwo,
            difficultyCalculator.difficulty,
            GameDifficulty.MEDIUM
        )
        setStarByDifficulty(
            binding.ratingStarThree,
            difficultyCalculator.difficulty,
            GameDifficulty.HARD
        )
        setStarByDifficulty(
            binding.ratingStarFour,
            difficultyCalculator.difficulty,
            GameDifficulty.EXTREME
        )
    }

    private fun setStarByDifficulty(
        view: ImageView,
        difficulty: GameDifficulty,
        minimumDifficulty: GameDifficulty
    ) {
        if (difficulty >= minimumDifficulty) {
            view.setImageResource(R.drawable.filled_star_20)
        } else {
            view.setImageResource(R.drawable.outline_star_20)
        }
    }

    override fun playTimeUpdated() {
        activity?.runOnUiThread {
            setGameTime(game.grid.playTime)
        }
    }

    fun setGameTime(gameDuration: Duration) {
        val durationString = Utils.displayableGameDuration(gameDuration)

        if (this::binding.isInitialized) {
            binding.playtime.text = durationString
        } else {
            this.timeDescription = durationString
        }
    }
}