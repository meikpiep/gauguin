package com.holokenmod.ui.main

import android.transition.TransitionManager
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.holokenmod.R
import com.holokenmod.game.Game
import com.holokenmod.options.ApplicationPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BottomAppBarItemClickistener(
    private val mainConstraintLayout: ConstraintLayout,
    private val mainActivity: MainActivity,
) : Toolbar.OnMenuItemClickListener, KoinComponent {
    private val game: Game by inject()
    private val applicationPreferences: ApplicationPreferences by inject()

    private var keypadFrameHorizontalBias = 0f

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        if (itemId == R.id.hint) {
            mainActivity.checkProgress()
        } else if (itemId == R.id.undo) {
            game.undo()
        } else if (itemId == R.id.eraser) {
            game.eraseSelectedCell()
        } else if (itemId == R.id.simulate_game_solved) {
            game.solveAllMissingCells()
        } else if (itemId == R.id.menu_show_mistakes) {
            game.markInvalidChoices(applicationPreferences.showDupedDigits())
            mainActivity.cheatedOnGame()
        } else if (itemId == R.id.menu_reveal_cell) {
            if (game.revealSelectedCell()) {
                mainActivity.cheatedOnGame()
            }
        } else if (itemId == R.id.menu_reveal_cage) {
            if (game.solveSelectedCage()) {
                mainActivity.cheatedOnGame()
            }
        } else if (itemId == R.id.menu_show_solution) {
            game.solveGrid()
            mainActivity.cheatedOnGame()
        } else if (itemId == R.id.menu_swap_keypad) {
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
        return true
    }

}
