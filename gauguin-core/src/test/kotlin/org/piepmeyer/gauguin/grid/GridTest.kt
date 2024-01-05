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

    test("correct and incorrect values in one row should lead to exactly 1 mistake") {
        val grid = smallGrid()

        grid.getValidCellAt(0, 0).userValue = 2
        grid.getValidCellAt(0, 1).userValue = 2

        println(grid)

        grid.numberOfMistakes() shouldBe 1
    }

    test("fillSingleCages") {
        val grid =
            GridBuilder(3)
                .addCage(1, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_HORIZONTAL, 0)
                .addSingleCage(2, 3)
                .addSingleCage(3, 4)
                .addSingleCage(4, 5)
                .addCage(1, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_HORIZONTAL, 6)
                .createGrid()

        grid.getCell(3).value = 2
        grid.getCell(4).value = 3
        grid.getCell(5).value = 4

        grid.fillSingleCages()

        grid.getCell(0).userValue shouldBe GridCell.NO_VALUE_SET
        grid.getCell(1).userValue shouldBe GridCell.NO_VALUE_SET
        grid.getCell(2).userValue shouldBe GridCell.NO_VALUE_SET
        grid.getCell(3).userValue shouldBe 2
        grid.getCell(4).userValue shouldBe 3
        grid.getCell(5).userValue shouldBe 4
        grid.getCell(6).userValue shouldBe GridCell.NO_VALUE_SET
        grid.getCell(7).userValue shouldBe GridCell.NO_VALUE_SET
        grid.getCell(8).userValue shouldBe GridCell.NO_VALUE_SET
    }
})

private fun smallGrid(): Grid {
    return GridBuilder(2)
        .addCage(2, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_BOTTOM, 0)
        .addSingleCage(2, 3)
        .addValueRow(2, 1)
        .addValueRow(1, 2)
        .createGrid()
}
