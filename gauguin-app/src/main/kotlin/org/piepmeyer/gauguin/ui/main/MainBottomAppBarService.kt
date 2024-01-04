package org.piepmeyer.gauguin.ui.main

import android.view.MenuItem
import android.view.View
import androidx.core.view.iterator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.ActivityMainBinding
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.undo.UndoListener
import org.piepmeyer.gauguin.undo.UndoManager

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

        val undoListener = UndoListener { undoPossible -> undoButton.isEnabled = undoPossible }
        val undoList = UndoManager(undoListener)

        game.undoManager = undoList

        binding.hintOrNewGame.setOnClickListener { mainActivity.checkProgressOrStartNewGame() }
        undoButton.setOnClickListener { game.undoOneStep() }
        eraserButton?.setOnClickListener { game.eraseSelectedCell() }

        eraserMenuItem?.setOnMenuItemClickListener {
            game.eraseSelectedCell()

            true
        }
    }

    fun updateAppBarState() {
        if (game.grid.isSolved()) {
            binding.hintOrNewGame.isEnabled = true
            binding.hintOrNewGame.setImageResource(R.drawable.outline_add_24)

            undoButton.visibility = View.GONE
            eraserButton?.visibility = View.GONE
        } else {
            binding.hintOrNewGame.isEnabled = true
            binding.hintOrNewGame.setImageResource(R.drawable.baseline_question_mark_24)

            undoButton.visibility = View.VISIBLE
            undoButton.isEnabled = false

            eraserButton?.visibility = View.VISIBLE
        }
    }
}
