package org.piepmeyer.gauguin.ui.main

import android.content.Context
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.creation.GridCalculatorFactory
import org.piepmeyer.gauguin.difficulty.human.HumanDifficultyCalculator
import org.piepmeyer.gauguin.difficulty.human.HumanSolver
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.game.GameSolveService
import org.piepmeyer.gauguin.grid.Grid

class BottomAppBarItemClickListener(
    private val context: Context,
) : Toolbar.OnMenuItemClickListener,
    KoinComponent {
    private val game: Game by inject()
    private val gameLifecycle: GameLifecycle by inject()
    private val gameSolveService: GameSolveService by inject()

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.undo -> game.undoOneStep()
            R.id.eraser -> game.eraseSelectedCell()
            R.id.simulate_game_solved -> game.solveAllMissingCells()
            R.id.menu_show_mistakes -> gameSolveService.markInvalidChoices()
            R.id.menu_reveal_cell -> gameSolveService.revealSelectedCell()
            R.id.menu_reveal_cage -> gameSolveService.revealSelectedCage()
            R.id.menu_show_solution -> gameSolveService.solveGrid()
            R.id.menu_debug_solve_by_human_solver_from_start -> {
                val solver = HumanSolver(game.grid)
                solver.prepareGrid()
                solver.solveAndCalculateDifficulty(true)

                game.gridUI.invalidate()
            }
            R.id.menu_debug_solve_by_human_solver_from_here -> {
                val solver = HumanSolver(game.grid)
                solver.solveAndCalculateDifficulty(true)

                game.gridUI.invalidate()
            }
            R.id.menu_debug_recalculate_difficulty -> {
                recalcuateDifficulty()
            }
            R.id.menu_debug_create_unsolved_grid -> {
                createUnsolvedGrid()
            }
        }

        return true
    }

    private fun recalcuateDifficulty() {
        val previousDifficulty = game.grid.difficulty.copy()

        game.grid.difficulty = game.grid.difficulty.copy(humanDifficulty = null)
        HumanDifficultyCalculator(game.grid).ensureDifficultyCalculated()

        val text =
            if (previousDifficulty != game.grid.difficulty) {
                "No changes."
            } else {
                "Previous difficulty ${previousDifficulty.humanDifficultyDisplayable()}, " +
                    "new difficulty ${game.grid.difficulty.humanDifficultyDisplayable()}."
            }

        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    private fun createUnsolvedGrid() {
        runBlocking {
            var grid: Grid?

            do {
                grid = GridCalculatorFactory().createCalculator(game.grid.variant).calculate()
                HumanDifficultyCalculator(grid).ensureDifficultyCalculated()
            } while (grid.difficulty.solvedViaHumanDifficulty == true)

            gameLifecycle.startNewGame(grid)
        }
    }
}
