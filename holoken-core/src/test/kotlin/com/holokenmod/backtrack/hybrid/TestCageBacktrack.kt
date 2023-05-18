package com.holokenmod.backtrack.hybrid

import com.holokenmod.creation.GridBuilder
import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridCage
import com.holokenmod.grid.GridCageAction
import com.holokenmod.grid.GridSize
import com.holokenmod.options.DigitSetting
import com.holokenmod.options.GameOptionsVariant.Companion.createClassic
import com.holokenmod.options.GameVariant
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TestCageBacktrack : FunSpec({
    test("firstGrid3x3") {
        /*  |     1-  0 |     3x  1 |         1 |
		    |         0 |     4x  2 |         2 |
    		|     3/  3 |         3 |         2 | */
        val builder = GridBuilder(3)
        builder.addCage(1, GridCageAction.ACTION_SUBTRACT, 0, 3)
            .addCage(3, GridCageAction.ACTION_MULTIPLY, 1, 2)
            .addCage(4, GridCageAction.ACTION_MULTIPLY, 4, 5, 8)
            .addCage(3, GridCageAction.ACTION_DIVIDE, 6, 7)
        val grid = builder.createGrid()
        println(grid.toString())
        grid.clearUserValues()
        val backtrack = MathDokuCage2BackTrack(grid, false)

        backtrack.solve() shouldBe 1
    }

    test("secondGrid3x3") {
        /*      |     3x  0 |         0 |    12x  1 |
    			|     5+  2 |         0 |         1 |
   				|         2 |         1 |         1 | */
        val builder = GridBuilder(3)
        builder.addCage(3, GridCageAction.ACTION_MULTIPLY, 0, 1, 4)
            .addCage(12, GridCageAction.ACTION_MULTIPLY, 2, 5, 7, 8)
            .addCage(5, GridCageAction.ACTION_ADD, 3, 6)
        val grid = builder.createGrid()
        println(grid.toString())
        grid.clearUserValues()
        val backtrack = MathDokuCage2BackTrack(grid, false)

        backtrack.solve() shouldBe 1
    }

    test("thirdGrid3x3") {
        /*  |     6+  0 |     7+  1 |         1 |
    		|         0 |         1 |         1 |
    		|         0 |     6x  2 |         2 | */
        val builder = GridBuilder(3)
        builder.addCage(6, GridCageAction.ACTION_ADD, 0, 3, 6)
            .addCage(7, GridCageAction.ACTION_ADD, 1, 2, 4, 5)
            .addCage(6, GridCageAction.ACTION_MULTIPLY, 7, 8)
        val grid = builder.createGrid()
        println(grid.toString())
        grid.clearUserValues()
        val backtrack = MathDokuCage2BackTrack(grid, false)

        backtrack.solve() shouldBe 2
    }

    context("4x4Grid") {
        test("grid1") {
            /*      |     2/  0 |         0 |     3+  1 |         1 |
    			|     0x  2 |     6+  3 |         3 |         3 |
   			 	|         2 |         2 |     6+  4 |         3 |
    			|     3-  5 |         5 |         4 |         4 |*/
            val builder = GridBuilder(4, DigitSetting.FIRST_DIGIT_ZERO)
            builder.addCage(2, GridCageAction.ACTION_DIVIDE, 0, 1)
                .addCage(3, GridCageAction.ACTION_ADD, 2, 3)
                .addCage(0, GridCageAction.ACTION_MULTIPLY, 4, 8, 9)
                .addCage(6, GridCageAction.ACTION_ADD, 5, 6, 7, 11)
                .addCage(6, GridCageAction.ACTION_ADD, 10, 14, 15)
                .addCage(3, GridCageAction.ACTION_SUBTRACT, 12, 13)
            val grid = builder.createGrid()
            println(grid.toString())
            grid.clearUserValues()

            val backtrack = MathDokuCage2BackTrack(grid, false)

            backtrack.solve() shouldBe 2
        }

        test("grid2") {
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

        test("grid3") {
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

        test("grid4") {
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
    }
})