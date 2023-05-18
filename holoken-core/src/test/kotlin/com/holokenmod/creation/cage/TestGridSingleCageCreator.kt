package com.holokenmod.creation.cage

import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridCage
import com.holokenmod.grid.GridCageAction
import com.holokenmod.grid.GridSize
import com.holokenmod.options.DigitSetting
import com.holokenmod.options.GameOptionsVariant.Companion.createClassic
import com.holokenmod.options.GameVariant
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TestGridSingleCageCreator : FunSpec({
    test("allDivideResultsWithoutZero") {
        val grid = Grid(
            GameVariant(
                GridSize(4, 4),
                createClassic()
            )
        )
        val cage = GridCage(0, grid, GridCageAction.ACTION_DIVIDE)
        cage.result = 2
        
        val creator = GridSingleCageCreator(grid, cage)
        
        creator.allDivideResults().size shouldBe 4
        
        creator.possibleNums[0][0] shouldBe 2
        creator.possibleNums[0][1] shouldBe 1
        creator.possibleNums[1][0] shouldBe 1
        creator.possibleNums[1][1] shouldBe 2
        creator.possibleNums[2][0] shouldBe 4
        creator.possibleNums[2][1] shouldBe 2
        creator.possibleNums[3][0] shouldBe 2
        creator.possibleNums[3][1] shouldBe 4
    }

    test("allDivideResultsWithZero") {
        val grid = Grid(
            GameVariant(
                GridSize(4, 4),
                createClassic(DigitSetting.FIRST_DIGIT_ZERO)
            )
        )
        val cage = GridCage(0, grid, GridCageAction.ACTION_DIVIDE)

        cage.result = 0
        val creator = GridSingleCageCreator(grid, cage)

        creator.allDivideResults().size shouldBe 6

        creator.possibleNums[0][0] shouldBe 1
        creator.possibleNums[0][1] shouldBe 0
        creator.possibleNums[1][0] shouldBe 0
        creator.possibleNums[1][1] shouldBe 1
        creator.possibleNums[2][0] shouldBe 2
        creator.possibleNums[2][1] shouldBe 0
        creator.possibleNums[3][0] shouldBe 0
        creator.possibleNums[3][1] shouldBe 2
        creator.possibleNums[4][0] shouldBe 3
        creator.possibleNums[4][1] shouldBe 0
        creator.possibleNums[5][0] shouldBe 0
        creator.possibleNums[5][1] shouldBe 3
    }

    test("allSubtractResults") {
        val grid = Grid(
            GameVariant(
                GridSize(4, 4),
                createClassic()
            )
        )
        val cage = GridCage(0, grid, GridCageAction.ACTION_SUBTRACT)
        cage.result = 2

        val creator = GridSingleCageCreator(grid, cage)

        creator.possibleNums.size shouldBe 4

        creator.possibleNums[0][0] shouldBe 1
        creator.possibleNums[0][1] shouldBe 3
        creator.possibleNums[1][0] shouldBe 2
        creator.possibleNums[1][1] shouldBe 4
        creator.possibleNums[2][0] shouldBe 3
        creator.possibleNums[2][1] shouldBe 1
        creator.possibleNums[3][0] shouldBe 4
        creator.possibleNums[3][1] shouldBe 2
    }
})