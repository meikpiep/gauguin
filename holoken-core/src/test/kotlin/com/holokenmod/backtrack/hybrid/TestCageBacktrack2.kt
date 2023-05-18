package com.holokenmod.backtrack.hybrid

import com.holokenmod.creation.GridBuilder
import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridCage
import com.holokenmod.grid.GridCageAction
import com.holokenmod.grid.GridSize
import com.holokenmod.options.DigitSetting
import com.holokenmod.options.GameOptionsVariant.Companion.createClassic
import com.holokenmod.options.GameVariant
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class TestCageBacktrack2 {
    @Test
    fun testFirstGrid() {
        /*  |     6x  0 |     4+  1 |     2/  2 |     0x  3 |
    		|         0 |         1 |         2 |         3 |
    		|     0x  4 |         1 |         3 |         3 |
    		|         4 |         4 |     3x  5 |         5 | */
        val builder = GridBuilder(4, DigitSetting.FIRST_DIGIT_ZERO)

        builder.addCage(6, GridCageAction.ACTION_MULTIPLY, 0, 4)
            .addCage(4, GridCageAction.ACTION_ADD, 1, 5, 9)
            .addCage(2, GridCageAction.ACTION_DIVIDE, 2, 6)
            .addCage(0, GridCageAction.ACTION_MULTIPLY, 3, 7, 10, 11)
            .addCage(0, GridCageAction.ACTION_MULTIPLY, 8, 12, 13)
            .addCage(3, GridCageAction.ACTION_MULTIPLY, 14, 15)

        val grid = builder.createGrid()
        println(grid.toString())

        val backtrack = MathDokuCage2BackTrack(grid, false)

        backtrack.solve() shouldBe 2
    }

    @Test
    fun testSecondGrid() {
        /*  |     1-  0 |     0x  1 |         1 |     6x  2 |
    		|         0 |         1 |         2 |         2 |
    		|     4+  3 |         3 |         3 |     3-  4 |
    		|     5+  5 |         5 |         3 |         4 |*/
        val builder = GridBuilder(4, DigitSetting.FIRST_DIGIT_ZERO)
        builder.addCage(1, GridCageAction.ACTION_SUBTRACT, 0, 4)
            .addCage(0, GridCageAction.ACTION_MULTIPLY, 1, 2, 5)
            .addCage(6, GridCageAction.ACTION_MULTIPLY, 3, 6, 7)
            .addCage(4, GridCageAction.ACTION_ADD, 8, 9, 10, 14)
            .addCage(3, GridCageAction.ACTION_SUBTRACT, 11, 15)
            .addCage(5, GridCageAction.ACTION_ADD, 12, 13)
        val grid = builder.createGrid()
        println(grid.toString())

        val backtrack = MathDokuCage2BackTrack(grid, false)

        backtrack.solve() shouldBe 2
    }

    @Test
    fun testThirdGrid() {
        /*  |    12x  0 |         0 |     1-  1 |     0x  2 |
    		|         0 |     4+  3 |         1 |         2 |
    		|         0 |         3 |    12x  4 |         2 |
    		|         3 |         3 |         4 |         4 |*/
        val grid = Grid(
            GameVariant(
                GridSize(4, 4),
                createClassic(DigitSetting.FIRST_DIGIT_ZERO)
            )
        )
        grid.addAllCells()

        var cage = GridCage(0, grid, GridCageAction.ACTION_MULTIPLY)
        cage.result = 12
        cage.addCell(grid.getCell(0))
        cage.addCell(grid.getCell(1))
        cage.addCell(grid.getCell(4))
        cage.addCell(grid.getCell(8))
        grid.addCage(cage)

        cage = GridCage(1, grid, GridCageAction.ACTION_SUBTRACT)
        cage.result = 1
        cage.addCell(grid.getCell(2))
        cage.addCell(grid.getCell(6))
        grid.addCage(cage)

        cage = GridCage(2, grid, GridCageAction.ACTION_MULTIPLY)
        cage.result = 0
        cage.addCell(grid.getCell(3))
        cage.addCell(grid.getCell(7))
        cage.addCell(grid.getCell(11))
        grid.addCage(cage)

        cage = GridCage(3, grid, GridCageAction.ACTION_ADD)
        cage.result = 4
        cage.addCell(grid.getCell(5))
        cage.addCell(grid.getCell(9))
        cage.addCell(grid.getCell(12))
        cage.addCell(grid.getCell(13))
        grid.addCage(cage)

        cage = GridCage(4, grid, GridCageAction.ACTION_MULTIPLY)
        cage.result = 12
        cage.addCell(grid.getCell(10))
        cage.addCell(grid.getCell(14))
        cage.addCell(grid.getCell(15))
        grid.addCage(cage)

        grid.setCageTexts()

        println(grid.toString())
        val backtrack = MathDokuCage2BackTrack(grid, false)

        backtrack.solve() shouldBe 2
    }

    @Disabled
    @Test
    fun testAnotherGrid() {
        /*  |     3+  0 |     7+  1 |     3/  2 |         2 |
    		|         0 |         1 |         1 |     6+  3 |
    		|         0 |     1-  4 |         4 |         3 |
    		|     0x  5 |         5 |         3 |         3 | */
        val grid = Grid(
            GameVariant(
                GridSize(4, 4),
                createClassic(DigitSetting.FIRST_DIGIT_ZERO)
            )
        )
        grid.addAllCells()
        var cage = GridCage(0, grid, GridCageAction.ACTION_ADD)
        cage.result = 3
        cage.addCell(grid.getCell(0))
        cage.addCell(grid.getCell(4))
        cage.addCell(grid.getCell(8))
        grid.addCage(cage)
        cage = GridCage(1, grid, GridCageAction.ACTION_ADD)
        cage.result = 7
        cage.addCell(grid.getCell(1))
        cage.addCell(grid.getCell(5))
        cage.addCell(grid.getCell(6))
        grid.addCage(cage)
        cage = GridCage(2, grid, GridCageAction.ACTION_DIVIDE)
        cage.result = 3
        cage.addCell(grid.getCell(2))
        cage.addCell(grid.getCell(3))
        grid.addCage(cage)
        cage = GridCage(3, grid, GridCageAction.ACTION_ADD)
        cage.result = 6
        cage.addCell(grid.getCell(7))
        cage.addCell(grid.getCell(11))
        cage.addCell(grid.getCell(14))
        cage.addCell(grid.getCell(15))
        grid.addCage(cage)
        cage = GridCage(4, grid, GridCageAction.ACTION_SUBTRACT)
        cage.result = 1
        cage.addCell(grid.getCell(9))
        cage.addCell(grid.getCell(10))
        grid.addCage(cage)
        cage = GridCage(5, grid, GridCageAction.ACTION_MULTIPLY)
        cage.result = 0
        cage.addCell(grid.getCell(12))
        cage.addCell(grid.getCell(13))
        grid.addCage(cage)

        grid.setCageTexts()
        println(grid.toString())

        val backtrack = MathDokuCage2BackTrack(grid, false)

        backtrack.solve() shouldBe 2
    }
}