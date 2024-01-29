package org.piepmeyer.gauguin.ui.main

import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameSolveService

class BottomAppBarItemClickListener(
    private val mainActivity: MainActivity,
) : Toolbar.OnMenuItemClickListener, KoinComponent {
    private val game: Game by inject()
    private val gameSolveService: GameSolveService by inject()

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.undo -> game.undoOneStep()
            R.id.eraser -> game.eraseSelectedCell()
            R.id.simulate_game_solved -> game.solveAllMissingCells()
            R.id.menu_show_mistakes -> {
                game.markInvalidChoices()
                mainActivity.cheatedOnGame()
            }

            R.id.menu_reveal_cell -> {
                if (gameSolveService.revealSelectedCell()) {
                    mainActivity.cheatedOnGame()
                }
            }

            R.id.menu_reveal_cage -> {
                if (gameSolveService.revealSelectedCage()) {
                    mainActivity.cheatedOnGame()
                }
            }

            R.id.menu_show_solution -> {
                gameSolveService.solveGrid()
                mainActivity.cheatedOnGame()
            }
        }

        return true
    }
}
