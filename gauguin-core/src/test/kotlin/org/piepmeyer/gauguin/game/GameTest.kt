package org.piepmeyer.gauguin.game

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.GridCageAction
import org.piepmeyer.gauguin.grid.GridCell

class GameTest : FunSpec({

    test("restart game clears all values and all possible values") {
        val smallGrid = GridBuilder(2)
            .addCage(2, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_BOTTOM, 0)
            .addSingleCage(2, 3)
            .createGrid()

        smallGrid.cells[0].userValue = 2
        smallGrid.cells[1].addPossible(1)
        smallGrid.cells[1].addPossible(2)

        val game = Game(
            grid = smallGrid,
            undoManager = mockk(relaxed = true),
            gridUI = mockk(relaxed = true)
        )

        game.restartGame()

        smallGrid.cells.forEach { it.userValue shouldBe GridCell.NO_VALUE_SET }
        smallGrid.cells.forEach { it.possibles.shouldBeEmpty() }
    }
})
