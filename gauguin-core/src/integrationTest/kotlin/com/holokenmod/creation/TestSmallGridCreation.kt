package com.holokenmod.creation

import com.holokenmod.creation.cage.GridCageType
import com.holokenmod.creation.dlx.DLX
import com.holokenmod.creation.dlx.MathDokuDLX
import com.holokenmod.grid.GridCageAction
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TestSmallGridCreation : FunSpec({
    test("firstGrid3x3") {
        /*  |     1-  0 |     3x  1 |         1 |
		    |         0 |     4x  2 |         2 |
    		|     3/  3 |         3 |         2 | */
        val builder = GridBuilder(3)
        builder.addCage(1, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_VERTICAL, 0)
            .addCage(3, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_HORIZONTAL, 1)
            .addCage(4, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_LEFT_BOTTOM, 4)
            .addCage(3, GridCageAction.ACTION_DIVIDE, GridCageType.DOUBLE_HORIZONTAL, 6)

        val grid = builder.createGrid()
        val mdd = MathDokuDLX(grid)

        println(grid)

        grid.getCell(8).cage().id shouldBe 2

        mdd.solve(DLX.SolveType.MULTIPLE) shouldBe 1
    }
})
