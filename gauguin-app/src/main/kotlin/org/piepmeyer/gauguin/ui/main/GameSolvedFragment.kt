package org.piepmeyer.gauguin.ui.main

import android.content.Intent
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
import org.piepmeyer.gauguin.Utils
import org.piepmeyer.gauguin.databinding.FragmentMainGameSolvedBinding
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.preferences.StatisticsManagerReading
import org.piepmeyer.gauguin.preferences.TypeOfSolution
import org.piepmeyer.gauguin.ui.statistics.StatisticsActivity
import kotlin.time.Duration.Companion.milliseconds

class GameSolvedFragment :
    Fragment(R.layout.fragment_main_game_solved),
    KoinComponent {
    private val game: Game by inject()
    private val gameLifecycle: GameLifecycle by inject()
    private val statisticsManager: StatisticsManagerReading by inject()
    private val viewModel: MainViewModel by inject()

    private lateinit var binding: FragmentMainGameSolvedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainGameSolvedBinding.inflate(inflater, parent, false)

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gameStateWithGrid.collect {
                    when (it.state) {
                        GameState.SOLVED, GameState.ALREADY_SOLVED -> {
                            puzzleSolved()

                            binding.gameSolvedCardView.visibility = View.VISIBLE
                        }
                        GameState.CALCULATING_NEW_GRID, GameState.PLAYING -> {
                            binding.gameSolvedCardView.visibility = View.GONE
                        }
                    }
                }
            }
        }

        binding.showStatisticsButton.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    requireActivity(),
                    StatisticsActivity::class.java,
                ),
            )
        }

        binding.playGameWithSameConfig.setOnClickListener {
            gameLifecycle.postNewGame(startedFromMainActivityWithSameVariant = true)
        }

        binding.playGameWithOtherConfig.setOnClickListener {
            (this.activity as MainActivity).showNewGameDialog()
        }
    }

    private fun puzzleSolved() {
        this.context?.let {
            val typeOfSolution =
                if (game.grid.isCheated()) {
                    TypeOfSolution.Regular
                } else {
                    statisticsManager.typeOfSolution(game.grid)
                }

            val icon =
                when (typeOfSolution) {
                    TypeOfSolution.FirstGame -> R.drawable.trophy_variant_outline
                    TypeOfSolution.FirstGameOfKind -> R.drawable.trophy_variant_outline
                    TypeOfSolution.BestTimeOfKind -> R.drawable.podium_gold
                    TypeOfSolution.Regular -> null
                }

            val text =
                when (typeOfSolution) {
                    TypeOfSolution.FirstGame -> it.getString(R.string.puzzle_solved_type_of_solution_first_game_solved)
                    TypeOfSolution.FirstGameOfKind -> it.getString(R.string.puzzle_solved_type_of_solution_first_game_of_kind_solved)
                    TypeOfSolution.BestTimeOfKind -> it.getString(R.string.puzzle_solved_type_of_solution_best_time_of_kind_solved)
                    TypeOfSolution.Regular -> {
                        val bestTime = statisticsManager.getBestTime(game.grid)

                        if (bestTime > 0.milliseconds) {
                            it.getString(
                                R.string.puzzle_solved_type_of_solution_regular_display_best_time,
                                Utils.displayableGameDuration(bestTime),
                            )
                        } else {
                            ""
                        }
                    }
                }

            if (icon != null) {
                binding.detailsIcon.setImageResource(icon)
                binding.detailsIcon.visibility = View.VISIBLE
            } else {
                binding.detailsIcon.visibility = View.INVISIBLE
            }

            binding.detailsText.text = text
            binding.detailsText.visibility = View.VISIBLE
        }
    }
}
