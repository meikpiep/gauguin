package org.piepmeyer.gauguin.ui.main

import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.difficulty.human.HumanSolver
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameSolveService

class BottomAppBarItemClickListener :
    Toolbar.OnMenuItemClickListener,
    KoinComponent {
    private val game: Game by inject()
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
        }

        return true
    }
}
