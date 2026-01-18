package org.piepmeyer.gauguin.ui.main

import android.content.Context
import android.content.DialogInterface
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.creation.GridCalculatorFactory
import org.piepmeyer.gauguin.difficulty.human.HumanDifficultyCalculatorImpl
import org.piepmeyer.gauguin.difficulty.human.HumanSolver
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.game.GameSolveService
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.preferences.StatisticsManagerReading
import java.lang.ref.WeakReference

class BottomAppBarItemClickListener(
    context: Context,
) : Toolbar.OnMenuItemClickListener,
    KoinComponent {
    private val game: Game by inject()
    private val gameLifecycle: GameLifecycle by inject()
    private val gameSolveService: GameSolveService by inject()
    private val statisticsManager: StatisticsManagerReading by inject()

    private val contextWeakReference: WeakReference<Context> = WeakReference(context)

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val context = contextWeakReference.get() ?: return false

        when (menuItem.itemId) {
            R.id.undo -> {
                game.undoOneStep()
            }

            R.id.eraser -> {
                game.eraseSelectedCell()
            }

            R.id.simulate_game_solved -> {
                game.solveAllMissingCells()
            }

            R.id.simulate_thousand_games -> {
                gameSolveService.simulateThousandGames()
            }

            R.id.menu_show_mistakes -> {
                askUserBeforeRevealingIfNecessary(context) { gameSolveService.markInvalidChoices() }
            }

            R.id.menu_reveal_cell -> {
                askUserBeforeRevealingIfNecessary(context) { gameSolveService.revealSelectedCell() }
            }

            R.id.menu_reveal_cage -> {
                askUserBeforeRevealingIfNecessary(context) { gameSolveService.revealSelectedCage() }
            }

            R.id.menu_show_solution -> {
                askUserBeforeRevealingIfNecessary(context) { gameSolveService.solveGrid() }
            }

            R.id.menu_debug_solve_by_human_solver_from_start -> {
                val solver = HumanSolver(game.grid, true)
                solver.prepareGrid()
                solver.solveAndCalculateDifficulty(true)

                game.gridUI.invalidate()
            }

            R.id.menu_debug_solve_by_human_solver_from_here -> {
                val solver = HumanSolver(game.grid, true)
                solver.solveAndCalculateDifficulty(true)

                game.gridUI.invalidate()
            }

            R.id.menu_debug_solve_by_human_solver_with_nishio_from_start -> {
                val solver = HumanSolver(game.grid)
                solver.prepareGrid()
                solver.solveAndCalculateDifficulty(true)

                game.gridUI.invalidate()
            }

            R.id.menu_debug_solve_by_human_solver_with_nishio_from_here -> {
                val solver = HumanSolver(game.grid)
                solver.solveAndCalculateDifficulty(true)

                game.gridUI.invalidate()
            }

            R.id.menu_debug_recalculate_difficulty -> {
                recalcuateDifficulty(context)
            }

            R.id.menu_debug_create_unsolved_grid -> {
                createUnsolvedGrid(context)
            }
        }

        return true
    }

    private fun askUserBeforeRevealingIfNecessary(
        context: Context,
        revealAction: () -> Unit,
    ) {
        if (!game.grid.isCheated() && statisticsManager.currentStreak() > 0) {
            askUserBeforeRevealing(context, revealAction)

            return
        }

        revealAction.invoke()
    }

    private fun askUserBeforeRevealing(
        context: Context,
        revealAction: () -> Unit,
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.main_activity_reveal_or_other_help_will_brake_streak_title)
            .setMessage(
                context.resources.getQuantityString(
                    R.plurals.main_activity_reveal_or_other_help_will_brake_streak_message,
                    statisticsManager.currentStreak(),
                    statisticsManager.currentStreak(),
                ),
            ).setNegativeButton(
                R.string.main_activity_reveal_or_other_help_will_brake_streak_cancel_button,
            ) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .setPositiveButton(R.string.main_activity_reveal_or_other_help_will_brake_streak_ok_button) { _: DialogInterface?, _: Int ->
                revealAction.invoke()
            }.show()
    }

    private fun recalcuateDifficulty(context: Context) {
        val previousDifficulty = game.grid.difficulty.copy()

        game.grid.difficulty = game.grid.difficulty.copy(humanDifficulty = null)
        HumanDifficultyCalculatorImpl(game.grid).ensureDifficultyCalculated()

        val text =
            if (previousDifficulty == game.grid.difficulty) {
                "No changes."
            } else {
                "Previous difficulty ${previousDifficulty.humanDifficultyDisplayable()}, " +
                    "new difficulty ${game.grid.difficulty.humanDifficultyDisplayable()}."
            }

        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    private fun createUnsolvedGrid(context: Context) {
        runBlocking {
            var grid: Grid?
            var tries = 0

            do {
                grid = GridCalculatorFactory().createCalculator(game.grid.variant).calculate()
                HumanDifficultyCalculatorImpl(grid).ensureDifficultyCalculated()
                tries++
            } while (grid.difficulty.solvedViaHumanDifficulty == true)

            gameLifecycle.startNewGame(grid)

            Toast.makeText(context, "Calculated unsolved grid after $tries grid(s) at all.", Toast.LENGTH_LONG).show()
        }
    }
}
