package org.piepmeyer.gauguin.ui.main

import android.view.KeyEvent
import android.view.View
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.grid.GridCell

class GridUIOnKeyListener(
    private val mainActivity: MainActivity,
) : KoinComponent,
    View.OnKeyListener {
    private val game: Game by inject()

    override fun onKey(
        v: View,
        keyCode: Int,
        event: KeyEvent,
    ): Boolean {
        if (!game.grid.isActive || event.action != KeyEvent.ACTION_DOWN) {
            return false
        }

        when {
            (event.keyCode == KeyEvent.KEYCODE_0 || event.number != '0') && event.number.isDigit() -> {
                val number = event.number.digitToInt()

                if (event.isMetaPressed || event.isShiftPressed) {
                    game.enterNumber(number)
                    game.gridUI.invalidate()

                    return true
                } else if (game.grid.variant.possibleDigits
                        .contains(number)
                ) {
                    game.enterPossibleNumber(number)

                    return true
                }
            }
            event.keyCode == KeyEvent.KEYCODE_ENTER || event.keyCode == KeyEvent.KEYCODE_SPACE -> {
                game.longClickOnSelectedCell()
                return true
            }
            event.keyCode == KeyEvent.KEYCODE_DEL -> {
                if (game.undoManager.undoPossible()) {
                    game.undoOneStep()
                    game.gridUI.invalidate()
                }
                return true
            }
            event.keyCode == KeyEvent.KEYCODE_FORWARD_DEL -> {
                game.grid.selectedCell?.clearPossibles()
                game.gridUI.invalidate()
                return true
            }
            event.unicodeChar == '?'.code -> {
                mainActivity.checkProgress()
                return true
            }
        }

        changedSelectedCellFromEvent(event)?.let {
            game.selectCell(it)
            game.gridUI.invalidate()

            return true
        }

        return isKeyCodeToMoveCursor(event)
    }

    private fun changedSelectedCellFromEvent(event: KeyEvent): GridCell? {
        val gridSize = game.grid.gridSize

        val selectedCell =
            game.grid.selectedCell
                ?: return if (isKeyCodeToMoveCursor(event)) {
                    game.grid.getCellAt(gridSize.height / 2, gridSize.width / 2)
                } else {
                    null
                }

        return when {
            (event.keyCode == KeyEvent.KEYCODE_DPAD_UP || event.keyCode == KeyEvent.KEYCODE_W) && selectedCell.row > 0 -> {
                game.grid.getCellAt(selectedCell.row - 1, selectedCell.column)
            }
            (event.keyCode == KeyEvent.KEYCODE_DPAD_DOWN || event.keyCode == KeyEvent.KEYCODE_S) &&
                selectedCell.row < game.grid.gridSize.height - 1 -> {
                game.grid.getCellAt(selectedCell.row + 1, selectedCell.column)
            }
            (event.keyCode == KeyEvent.KEYCODE_DPAD_LEFT || event.keyCode == KeyEvent.KEYCODE_A) && selectedCell.column > 0 -> {
                game.grid.getCellAt(selectedCell.row, selectedCell.column - 1)
            }
            (event.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || event.keyCode == KeyEvent.KEYCODE_D) &&
                selectedCell.column < game.grid.gridSize.width - 1 -> {
                game.grid.getCellAt(selectedCell.row, selectedCell.column + 1)
            }
            else ->
                null
        }
    }

    private fun isKeyCodeToMoveCursor(event: KeyEvent) =
        event.keyCode in
            listOf(
                KeyEvent.KEYCODE_DPAD_UP,
                KeyEvent.KEYCODE_DPAD_DOWN,
                KeyEvent.KEYCODE_DPAD_LEFT,
                KeyEvent.KEYCODE_DPAD_RIGHT,
                KeyEvent.KEYCODE_W,
                KeyEvent.KEYCODE_A,
                KeyEvent.KEYCODE_S,
                KeyEvent.KEYCODE_D,
            )
}
