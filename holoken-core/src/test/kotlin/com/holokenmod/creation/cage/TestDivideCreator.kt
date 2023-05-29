package com.holokenmod.creation.cage

import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridSize
import com.holokenmod.options.DigitSetting
import com.holokenmod.options.GameOptionsVariant.Companion.createClassic
import com.holokenmod.options.GameVariant
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TestDivideCreator : FunSpec({
    test("allDivideResultsWithoutZero") {
        val grid = Grid(
            GameVariant(
                GridSize(4, 4),
                createClassic()
            )
        )

        val possibleNums = DivideCreator(grid, 2).create()

        possibleNums.size shouldBe 4

        possibleNums[0][0] shouldBe 2
        possibleNums[0][1] shouldBe 1
        possibleNums[1][0] shouldBe 1
        possibleNums[1][1] shouldBe 2
        possibleNums[2][0] shouldBe 4
        possibleNums[2][1] shouldBe 2
        possibleNums[3][0] shouldBe 2
        possibleNums[3][1] shouldBe 4
    }

    test("allDivideResultsWithZero") {
        val grid = Grid(
            GameVariant(
                GridSize(4, 4),
                createClassic(DigitSetting.FIRST_DIGIT_ZERO)
            )
        )

        val possibleNums = DivideCreator(grid, 0).create()

        possibleNums.size shouldBe 6

        possibleNums[0][0] shouldBe 1
        possibleNums[0][1] shouldBe 0
        possibleNums[1][0] shouldBe 0
        possibleNums[1][1] shouldBe 1
        possibleNums[2][0] shouldBe 2
        possibleNums[2][1] shouldBe 0
        possibleNums[3][0] shouldBe 0
        possibleNums[3][1] shouldBe 2
        possibleNums[4][0] shouldBe 3
        possibleNums[4][1] shouldBe 0
        possibleNums[5][0] shouldBe 0
        possibleNums[5][1] shouldBe 3
    }
})
