package com.holokenmod.grid

import com.holokenmod.creation.GridBuilder
import com.holokenmod.creation.cage.GridCageType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GridTest : FunSpec({

    test("same number in row leads to duplicate number") {
        val grid = smallGrid()

        grid.getCellAt(0, 0)!!.setUserValueExtern(2)
        grid.getCellAt(0, 1)!!.setUserValueExtern(2)

        grid.userValueChanged()

        grid.getCellAt(0, 0)!!.duplicatedInRowOrColumn shouldBe true
        grid.getCellAt(0, 1)!!.duplicatedInRowOrColumn shouldBe true
        grid.getCellAt(1, 0)!!.duplicatedInRowOrColumn shouldBe false
        grid.getCellAt(1, 1)!!.duplicatedInRowOrColumn shouldBe false
    }

    test("same number in column leads to duplicate number") {
        val grid = smallGrid()

        grid.getCellAt(0, 0)!!.setUserValueExtern(2)
        grid.getCellAt(1, 0)!!.setUserValueExtern(2)

        grid.userValueChanged()

        grid.getCellAt(0, 0)!!.duplicatedInRowOrColumn shouldBe true
        grid.getCellAt(1, 0)!!.duplicatedInRowOrColumn shouldBe true
        grid.getCellAt(0, 1)!!.duplicatedInRowOrColumn shouldBe false
        grid.getCellAt(1, 1)!!.duplicatedInRowOrColumn shouldBe false
    }

    test("same number in row which gets cleared leads to no duplicate number") {
        val grid = smallGrid()

        grid.getCellAt(0, 0)!!.setUserValueExtern(2)
        grid.getCellAt(0, 1)!!.setUserValueExtern(2)

        grid.userValueChanged()

        grid.getCellAt(0, 1)!!.clearUserValue()
        grid.userValueChanged()

        grid.getCellAt(0, 0)!!.duplicatedInRowOrColumn shouldBe false
        grid.getCellAt(0, 1)!!.duplicatedInRowOrColumn shouldBe false
        grid.getCellAt(1, 0)!!.duplicatedInRowOrColumn shouldBe false
        grid.getCellAt(1, 1)!!.duplicatedInRowOrColumn shouldBe false
    }
})

private fun smallGrid(): Grid {
    return GridBuilder(2)
        .addCage(2, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_BOTTOM, 0)
        .addSingleCage(2, 3)
        .createGrid()
}
