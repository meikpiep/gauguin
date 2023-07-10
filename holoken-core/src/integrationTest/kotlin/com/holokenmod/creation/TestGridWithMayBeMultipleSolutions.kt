package com.holokenmod.creation

import com.holokenmod.creation.cage.GridCageType
import com.holokenmod.grid.GridCageAction
import com.srlee.dlx.DLX
import com.srlee.dlx.MathDokuDLX
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TestGridWithMayBeMultipleSolutions : FunSpec({
    test("test") {
        val builder = GridBuilder(9, 9)

        builder.addCage(6, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_LEFT_BOTTOM, 0)
        builder.addCage(6, GridCageAction.ACTION_NONE, GridCageType.SINGLE, 2)
        builder.addCage(16, GridCageAction.ACTION_ADD, GridCageType.ANGLE_LEFT_TOP, 3)
        builder.addCage(40, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_HORIZONTAL, 4)
        builder.addCage(25, GridCageAction.ACTION_ADD, GridCageType.L_VERTICAL_SHORT_RIGHT_TOP, 7)
        builder.addCage(5, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_VERTICAL, 9)
        builder.addCage(4, GridCageAction.ACTION_NONE, GridCageType.SINGLE, 13)
        builder.addCage(8, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_HORIZONTAL, 14)
        builder.addCage(23, GridCageAction.ACTION_ADD, GridCageType.TRIPLE_VERTICAL, 17)
        builder.addCage(4, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_HORIZONTAL, 19)
        builder.addCage(11, GridCageAction.ACTION_ADD, GridCageType.L_VERTICAL_SHORT_RIGHT_TOP, 21)
        builder.addCage(3, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_HORIZONTAL, 23)
        builder.addCage(2, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_HORIZONTAL, 27)
        builder.addCage(70, GridCageAction.ACTION_MULTIPLY, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM, 29)
        builder.addCage(168, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_TOP, 31)
        builder.addCage(5, GridCageAction.ACTION_NONE, GridCageType.SINGLE, 32)
        builder.addCage(48, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_HORIZONTAL, 33)
        builder.addCage(2, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_HORIZONTAL, 36)
        builder.addCage(45, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_LEFT_BOTTOM, 42)
        builder.addCage(24, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_VERTICAL, 44)
        builder.addCage(210, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_VERTICAL, 45)
        builder.addCage(216, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_HORIZONTAL, 48)
        builder.addCage(18, GridCageAction.ACTION_ADD, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM, 51)
        builder.addCage(18, GridCageAction.ACTION_ADD, GridCageType.ANGLE_RIGHT_BOTTOM, 55)
        builder.addCage(7, GridCageAction.ACTION_NONE, GridCageType.SINGLE, 57)
        builder.addCage(21, GridCageAction.ACTION_ADD, GridCageType.ANGLE_RIGHT_BOTTOM, 58)
        builder.addCage(216, GridCageAction.ACTION_MULTIPLY, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM, 61)
        builder.addCage(10, GridCageAction.ACTION_ADD, GridCageType.ANGLE_LEFT_BOTTOM, 65)
        builder.addCage(5, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_VERTICAL, 71)
        builder.addCage(504, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_HORIZONTAL, 72)
        builder.addCage(7, GridCageAction.ACTION_ADD, GridCageType.DOUBLE_HORIZONTAL, 76)

        builder.addValueRow(1, 3, 6, 8, 2, 4, 5, 9, 7)
        builder.addValueRow(8, 2, 3, 5, 4, 9, 1, 7, 6)
        builder.addValueRow(3, 5, 9, 6, 1, 7, 4, 2, 8)
        builder.addValueRow(2, 4, 7, 1, 3, 5, 6, 8, 9)
        builder.addValueRow(4, 6, 5, 3, 7, 8, 9, 1, 2)
        builder.addValueRow(6, 1, 2, 9, 8, 3, 7, 5, 4)
        builder.addValueRow(5, 8, 1, 7, 9, 6, 2, 4, 3)
        builder.addValueRow(7, 9, 4, 2, 6, 1, 8, 3, 5)
        builder.addValueRow(9, 7, 8, 4, 5, 2, 3, 6, 1)

        val grid = builder.createGrid()
        val mdd = MathDokuDLX(grid)

        println(grid)

        mdd.solve(DLX.SolveType.MULTIPLE) shouldBe 1
    }
})
