package com.holokenmod.creation.cage

import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridSize
import com.holokenmod.options.GameOptionsVariant.Companion.createClassic
import com.holokenmod.options.GameVariant
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TestSubtractionCreator : FunSpec({
    test("allSubtractResults") {
        val grid = Grid(
            GameVariant(
                GridSize(4, 4),
                createClassic()
            )
        )

        val possibleNums = SubtractionCreator(grid, 2).create()

        possibleNums.size shouldBe 4

        possibleNums[0][0] shouldBe 1
        possibleNums[0][1] shouldBe 3
        possibleNums[1][0] shouldBe 2
        possibleNums[1][1] shouldBe 4
        possibleNums[2][0] shouldBe 3
        possibleNums[2][1] shouldBe 1
        possibleNums[3][0] shouldBe 4
        possibleNums[3][1] shouldBe 2
    }
})
