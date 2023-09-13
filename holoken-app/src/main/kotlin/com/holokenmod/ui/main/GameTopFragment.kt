package com.holokenmod.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.holokenmod.R
import com.holokenmod.creation.GridDifficultyCalculator
import com.holokenmod.databinding.GameTopFragmentBinding
import com.holokenmod.game.Game
import com.holokenmod.game.GridCreationListener
import com.holokenmod.options.GameDifficulty
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GameTopFragment : Fragment(R.layout.game_top_fragment), GridCreationListener, KoinComponent {
    private val game: Game by inject()
    private lateinit var binding: GameTopFragmentBinding

    private var timeDescription: String? = null
    private var showtimer = false

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GameTopFragmentBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        game.addGridCreationListener(this)

        freshGridWasCreated()
    }

    override fun freshGridWasCreated() {
        if (!isAdded) {
            return
        }
        requireActivity().runOnUiThread {
            val difficultyCalculator = GridDifficultyCalculator(game.grid)
            binding.difficulty.text = difficultyCalculator.info()
            setStarsByDifficulty(difficultyCalculator)
            if (showtimer) {
                binding.playtime.visibility = View.VISIBLE
            } else {
                binding.playtime.visibility = View.INVISIBLE
            }

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

    fun setGameTime(timeDescription: String) {
        if (this::binding.isInitialized) {
            binding.playtime.text = timeDescription
        } else {
            this.timeDescription = timeDescription
        }
    }

    fun setTimerVisible(showtimer: Boolean) {
        this.showtimer = showtimer
    }
}