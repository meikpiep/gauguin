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
import com.holokenmod.options.GameDifficulty
import com.holokenmod.ui.GridCreationListener

class GameTopFragment : Fragment(R.layout.game_top_fragment), GridCreationListener {
    private var game: Game? = null
    private var binding: GameTopFragmentBinding? = null
    private var showtimer = false
    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GameTopFragmentBinding.inflate(inflater, parent, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (game != null) {
            freshGridWasCreated()
        }
    }

    override fun freshGridWasCreated() {
        if (!isAdded) {
            return
        }
        requireActivity().runOnUiThread {
            val difficultyCalculator = GridDifficultyCalculator(game!!.grid)
            binding!!.difficulty.text = difficultyCalculator.info
            setStarsByDifficulty(difficultyCalculator)
            if (showtimer) {
                binding!!.playtime.visibility = View.VISIBLE
            } else {
                binding!!.playtime.visibility = View.INVISIBLE
            }
        }
    }

    fun setGame(game: Game) {
        this.game = game
        freshGridWasCreated()
    }

    private fun setStarsByDifficulty(difficultyCalculator: GridDifficultyCalculator) {
        setStarByDifficulty(
            binding!!.ratingStarOne,
            difficultyCalculator.difficulty,
            GameDifficulty.EASY
        )
        setStarByDifficulty(
            binding!!.ratingStarTwo,
            difficultyCalculator.difficulty,
            GameDifficulty.MEDIUM
        )
        setStarByDifficulty(
            binding!!.ratingStarThree,
            difficultyCalculator.difficulty,
            GameDifficulty.HARD
        )
        setStarByDifficulty(
            binding!!.ratingStarFour,
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

    fun setGameTime(timeDescription: String?) {
        //TODO Warum ist der Wert manchmal null? Folgefehler?
        if (binding == null)
            return
        binding!!.playtime.text = timeDescription
    }

    fun setTimerVisible(showtimer: Boolean) {
        this.showtimer = showtimer
    }
}