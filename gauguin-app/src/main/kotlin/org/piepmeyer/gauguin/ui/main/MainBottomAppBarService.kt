package org.piepmeyer.gauguin.ui.main

import android.view.MenuItem
import android.view.View
import androidx.core.view.iterator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.ActivityMainBinding
import org.piepmeyer.gauguin.game.Game

class MainBottomAppBarService(
    private val mainActivity: MainActivity,
    private val binding: ActivityMainBinding,
) : KoinComponent {
    private val game: Game by inject()

    private var undoButton: View? = null
    private var undoMenuItem: MenuItem? = null

    private var eraserButton: View? = null
    private var eraserMenuItem: MenuItem? = null

    fun initialize() {
        undoButton = mainActivity.findViewById(R.id.undo)

        /*
         * Depending on the available size, the app bar decides to create the eraser either as a
         * button or a menu item. So we have to handle both cases.
         */
        eraserButton = mainActivity.findViewById(R.id.eraser)

        binding.mainBottomAppBar.menu.iterator().forEach {
            when (it.itemId) {
                R.id.eraser -> eraserMenuItem = it
                R.id.undo -> undoMenuItem = it
            }
        }

        game.undoManager.addListener { undoPossible ->
            undoButton?.isEnabled = undoPossible
            undoMenuItem?.isEnabled = undoPossible
        }

        binding.hint.setOnClickListener { mainActivity.checkProgress() }

        undoButton?.setOnClickListener { game.undoOneStep() }
        undoMenuItem?.setOnMenuItemClickListener {
            game.undoOneStep()

            true
        }

        eraserButton?.setOnClickListener { game.eraseSelectedCell() }
        eraserMenuItem?.setOnMenuItemClickListener {
            game.eraseSelectedCell()

            true
        }
    }

    fun updateAppBarState(state: GameState) {
        if (state == GameState.PLAYING) {
            binding.hint.isEnabled = true
            binding.hint.show()

            undoButton?.isEnabled = game.undoManager.undoPossible()
            undoMenuItem?.isVisible = true
            eraserButton?.isEnabled = true
            eraserMenuItem?.isVisible = true

            solveHelperMenuItems().forEach { it.setVisible(true) }
        } else {
            binding.hint.hide()

            undoButton?.isEnabled = false
            undoMenuItem?.isVisible = false
            eraserButton?.isEnabled = false
            eraserMenuItem?.isVisible = false

            binding.mainBottomAppBar.menu.iterator().forEach {
                if (it.itemId == R.id.undo) {
                    it.isVisible = false
                }
            }
            solveHelperMenuItems().forEach { it.setVisible(false) }
        }
    }

    private fun solveHelperMenuItems(): List<MenuItem> {
        val menuItems = mutableListOf<MenuItem>()
        binding.mainBottomAppBar.menu.iterator().forEach {
            if (it.itemId in
                listOf(
                    R.id.menu_show_solution,
                    R.id.menu_reveal_cage,
                    R.id.menu_reveal_cell,
                    R.id.menu_show_mistakes,
                )
            ) {
                menuItems += it
            }
        }

        return menuItems.toList()
    }
}
