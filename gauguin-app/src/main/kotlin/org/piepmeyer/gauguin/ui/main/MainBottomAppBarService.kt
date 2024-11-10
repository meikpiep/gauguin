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

    private lateinit var undoButton: View
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
            if (it.itemId == R.id.eraser) {
                eraserMenuItem = it
                return@forEach
            }
        }

        game.undoManager.addListener { undoPossible ->
            undoButton.isEnabled = undoPossible
        }

        binding.hint.setOnClickListener { mainActivity.checkProgress() }
        undoButton.setOnClickListener { game.undoOneStep() }
        eraserButton?.setOnClickListener { game.eraseSelectedCell() }

        eraserMenuItem?.setOnMenuItemClickListener {
            game.eraseSelectedCell()

            true
        }
    }

    fun updateAppBarState() {
        if (game.grid.isSolved()) {
            binding.hint.hide()

            undoButton.visibility = View.GONE
            eraserButton?.visibility = View.GONE

            solveHelperMenuItems().forEach { it.setVisible(false) }
        } else {
            binding.hint.isEnabled = true
            binding.hint.show()

            undoButton.visibility = View.VISIBLE
            undoButton.isEnabled = game.undoManager.undoPossible()

            solveHelperMenuItems().forEach { it.setVisible(true) }
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
