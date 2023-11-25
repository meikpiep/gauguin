package org.piepmeyer.gauguin.ui.main

import android.transition.TransitionManager
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.preferences.ApplicationPreferencesImpl

class BottomAppBarItemClickListener(
    private val mainConstraintLayout: ConstraintLayout,
    private val mainActivity: MainActivity,
) : Toolbar.OnMenuItemClickListener, KoinComponent {
    private val game: Game by inject()
    private val applicationPreferences: ApplicationPreferencesImpl by inject()

    private var keypadFrameHorizontalBias = 0f

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.hintOrNewGame -> mainActivity.checkProgressOrStartNewGame()
            R.id.undo -> game.undoOneStep()
            R.id.eraser -> game.eraseSelectedCell()
            R.id.simulate_game_solved -> game.solveAllMissingCells()
            R.id.menu_show_mistakes -> {
                game.markInvalidChoices(applicationPreferences.showDupedDigits())
                mainActivity.cheatedOnGame()
            }

            R.id.menu_reveal_cell -> {
                if (game.revealSelectedCell()) {
                    mainActivity.cheatedOnGame()
                }
            }

            R.id.menu_reveal_cage -> {
                if (game.revealSelectedCage()) {
                    mainActivity.cheatedOnGame()
                }
            }

            R.id.menu_show_solution -> {
                game.solveGrid()
                mainActivity.cheatedOnGame()
            }

            R.id.menu_swap_keypad -> {
                keypadFrameHorizontalBias += 0.25f
                if (keypadFrameHorizontalBias == 1.0f) {
                    keypadFrameHorizontalBias = 0.25f
                }
                val constraintSet = ConstraintSet()
                constraintSet.clone(mainConstraintLayout)
                constraintSet.setHorizontalBias(R.id.keypadFrame, keypadFrameHorizontalBias)
                TransitionManager.beginDelayedTransition(mainConstraintLayout)
                constraintSet.applyTo(mainConstraintLayout)
            }
        }

        return true
    }

}
