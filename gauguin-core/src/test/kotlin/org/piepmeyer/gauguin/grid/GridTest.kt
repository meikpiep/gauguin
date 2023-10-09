package org.piepmeyer.gauguin.grid

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType

class GridTest : FunSpec({

    test("same number in row leads to duplicate number") {
        val grid = smallGrid()

        grid.getValidCellAt(0, 0).setUserValueExtern(2)
        grid.getValidCellAt(0, 1).setUserValueExtern(2)

        grid.userValueChanged()

        grid.getValidCellAt(0, 0).duplicatedInRowOrColumn shouldBe true
        grid.getValidCellAt(0, 1).duplicatedInRowOrColumn shouldBe true
        grid.getValidCellAt(1, 0).duplicatedInRowOrColumn shouldBe false
        grid.getValidCellAt(1, 1).duplicatedInRowOrColumn shouldBe false
    }

    test("same number in column leads to duplicate number") {
        val grid = smallGrid()

        grid.getValidCellAt(0, 0).setUserValueExtern(2)
        grid.getValidCellAt(1, 0).setUserValueExtern(2)

        grid.userValueChanged()

        grid.getValidCellAt(0, 0).duplicatedInRowOrColumn shouldBe true
        grid.getValidCellAt(1, 0).duplicatedInRowOrColumn shouldBe true
        grid.getValidCellAt(0, 1).duplicatedInRowOrColumn shouldBe false
        grid.getValidCellAt(1, 1).duplicatedInRowOrColumn shouldBe false
    }

    test("same number in row which gets cleared leads to no duplicate number") {
        val grid = smallGrid()

        grid.getValidCellAt(0, 0).setUserValueExtern(2)
        grid.getValidCellAt(0, 1).setUserValueExtern(2)

        grid.userValueChanged()

        grid.getValidCellAt(0, 1).clearUserValue()
        grid.userValueChanged()

        grid.getValidCellAt(0, 0).duplicatedInRowOrColumn shouldBe false
        grid.getValidCellAt(0, 1).duplicatedInRowOrColumn shouldBe false
        grid.getValidCellAt(1, 0).duplicatedInRowOrColumn shouldBe false
        grid.getValidCellAt(1, 1).duplicatedInRowOrColumn shouldBe false
    }
})

private fun smallGrid(): Grid {
    return GridBuilder(2)
        .addCage(2, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_BOTTOM, 0)
        .addSingleCage(2, 3)
        .createGrid()
}
