package com.holokenmod.creation

import com.holokenmod.grid.GridCageAction
import com.srlee.dlx.DLX
import com.srlee.dlx.MathDokuDLX
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TestGridWithMayBeMultipleSolutions {
    @Test
    fun test() {
        val builder = GridBuilder(9, 9)

        builder.addCage(6, GridCageAction.ACTION_MULTIPLY, 0, 1, 10)
        builder.addCage(6, GridCageAction.ACTION_NONE, 2)
        builder.addCage(16, GridCageAction.ACTION_ADD, 3, 11, 12)
        builder.addCage(40, GridCageAction.ACTION_MULTIPLY, 4, 5, 6)
        builder.addCage(25, GridCageAction.ACTION_ADD, 7, 8, 16, 25)
        builder.addCage(5, GridCageAction.ACTION_SUBTRACT, 9, 18)
        builder.addCage(4, GridCageAction.ACTION_NONE, 13)
        builder.addCage(8, GridCageAction.ACTION_SUBTRACT, 14, 15)
        builder.addCage(23, GridCageAction.ACTION_ADD, 17, 26, 35)
        builder.addCage(4, GridCageAction.ACTION_SUBTRACT, 19, 20)
        builder.addCage(11, GridCageAction.ACTION_ADD, 21, 22, 30, 39)
        builder.addCage(3, GridCageAction.ACTION_SUBTRACT, 23, 24)
        builder.addCage(2, GridCageAction.ACTION_SUBTRACT, 27, 28)
        builder.addCage(70, GridCageAction.ACTION_MULTIPLY, 29, 38, 46, 47)
        builder.addCage(168, GridCageAction.ACTION_MULTIPLY, 31, 40, 41)
        builder.addCage(5, GridCageAction.ACTION_NONE, 32)
        builder.addCage(48, GridCageAction.ACTION_MULTIPLY, 33, 34)
        builder.addCage(2, GridCageAction.ACTION_SUBTRACT, 36, 37)
        builder.addCage(45, GridCageAction.ACTION_MULTIPLY, 42, 43, 52)
        builder.addCage(24, GridCageAction.ACTION_MULTIPLY, 44, 53, 62)
        builder.addCage(210, GridCageAction.ACTION_MULTIPLY, 45, 54, 63)
        builder.addCage(216, GridCageAction.ACTION_MULTIPLY, 48, 49, 50)
        builder.addCage(18, GridCageAction.ACTION_ADD, 51, 60, 68, 69)
        builder.addCage(18, GridCageAction.ACTION_ADD, 55, 56, 64)
        builder.addCage(7, GridCageAction.ACTION_NONE, 57)
        builder.addCage(21, GridCageAction.ACTION_ADD, 58, 59, 67)
        builder.addCage(216, GridCageAction.ACTION_MULTIPLY, 61, 70, 78, 79)
        builder.addCage(10, GridCageAction.ACTION_ADD, 65, 66, 75)
        builder.addCage(5, GridCageAction.ACTION_MULTIPLY, 71, 80)
        builder.addCage(504, GridCageAction.ACTION_MULTIPLY, 72, 73, 74)
        builder.addCage(7, GridCageAction.ACTION_ADD, 76, 77)

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

        mdd.Solve(DLX.SolveType.MULTIPLE) shouldBe 1
    }
}