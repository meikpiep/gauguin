package com.holokenmod.backtrack.hybrid

import com.holokenmod.creation.GridBuilder
import com.holokenmod.creation.cage.GridCageType
import com.holokenmod.grid.GridCageAction
import com.srlee.dlx.DLX
import com.srlee.dlx.MathDokuDLX
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TestDLX : FunSpec({
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
        println(grid.toString())
        grid.clearUserValues()
        val backtrack = MathDokuDLX(grid)

        backtrack.solve(DLX.SolveType.MULTIPLE) shouldBe 1
    }
})
