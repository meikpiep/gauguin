package com.holokenmod.backtrack.hybrid

import com.holokenmod.creation.GridBuilder
import com.holokenmod.creation.cage.GridCageType
import com.holokenmod.grid.GridCageAction
import com.holokenmod.options.DigitSetting
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class TestGridsRectingular : FunSpec({
    val solverFactories = listOf(DlxFactory(), Cage2BackTrackFactory())

    context("2x3 grid") {
        withData(solverFactories) { solverFactory ->
            /*  |    12x  0 |     1   1 |
                |         0 |         0 |
                |     3x  2 |         2 | */
            val builder = GridBuilder(2, 3)
            builder.addCage(12, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_TOP, 0)
                .addCage(1, GridCageAction.ACTION_NONE, GridCageType.SINGLE, 1)
                .addCage(3, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_HORIZONTAL, 4)
            val grid = builder.createGrid()
            println(grid.toString())
            grid.clearUserValues()

            solverFactory.createSolver().solve(grid, false) shouldBe 1
        }
    }

    context("2x3 grid with zero") {
        withData(solverFactories) { solverFactory ->
            /*  |     2x  0 |         0 |
                |         0 |    2+   1 |
                |     3x  1 |         1 | */
            val builder = GridBuilder(2, 3, DigitSetting.FIRST_DIGIT_ZERO)
            builder.addCage(2, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_BOTTOM, 0)
                .addCage(2, GridCageAction.ACTION_ADD, GridCageType.ANGLE_LEFT_TOP, 3)
            val grid = builder.createGrid()
            println(grid.toString())
            grid.clearUserValues()

            solverFactory.createSolver().solve(grid, false) shouldBe 1
        }
    }

    context("2x3 grid with digits from -5 on") {
        withData(solverFactories) { solverFactory ->
            /*  |   -48x  0 |         0 |
                |    15x  1 |         0 |
                |         1 |    5-   2 | */
            val builder = GridBuilder(2, 3, DigitSetting.FIRST_DIGIT_MINUS_FIVE)
            builder.addCage(-48, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_LEFT_BOTTOM, 0)
                .addCage(15, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_VERTICAL, 2)
                .addCage(-5, GridCageAction.ACTION_NONE, GridCageType.SINGLE, 5)
            val grid = builder.createGrid()
            println(grid.toString())
            grid.clearUserValues()

            solverFactory.createSolver().solve(grid, false) shouldBe 1
        }
    }

    context("3x2 grid") {
        withData(solverFactories) { solverFactory ->
            /*  |     1   0 |   12x   1 |    3x   2 |
                |         1 |         1 |     1   2 | */
            val builder = GridBuilder(3, 2)
            builder.addCage(1, GridCageAction.ACTION_NONE, GridCageType.SINGLE, 0)
                .addCage(12, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_LEFT_TOP, 1)
                .addCage(3, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_VERTICAL, 2)
            val grid = builder.createGrid()
            println(grid.toString())
            grid.clearUserValues()

            solverFactory.createSolver().solve(grid, false) shouldBe 1
        }
    }

    context("2x4 grid") {
        withData(solverFactories) { solverFactory ->
            /*  |     4x  0 |         0 |
                |         0 |     8+  1 |
                |         1 |         1 |
                |     2/  2 |         2 | */
            val builder = GridBuilder(2, 4)
            builder.addCage(4, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_BOTTOM, 0)
                .addCage(8, GridCageAction.ACTION_ADD, GridCageType.ANGLE_LEFT_TOP, 3)
                .addCage(2, GridCageAction.ACTION_DIVIDE, GridCageType.DOUBLE_HORIZONTAL, 6)
            val grid = builder.createGrid()
            println(grid.toString())
            grid.clearUserValues()

            solverFactory.createSolver().solve(grid, false) shouldBe 1
        }
    }

    context("2x6 grid") {
        withData(solverFactories) { solverFactory ->
            /*  |     8x  0 |     2   1 |
                |         0 |         0 |
                |     5-  2 |         2 |
                |    75x  3 |     6   4 |
                |         3 |         3 |
                |    12x  5 |         5 | */
            val builder = GridBuilder(2, 6)
            builder.addCage(8, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_TOP, 0)
                .addCage(2, GridCageAction.ACTION_NONE, GridCageType.SINGLE, 1)
                .addCage(5, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_HORIZONTAL, 4)
                .addCage(75, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_TOP, 6)
                .addCage(6, GridCageAction.ACTION_NONE, GridCageType.SINGLE, 7)
                .addCage(12, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_HORIZONTAL, 10)
            val grid = builder.createGrid()
            println(grid.toString())
            grid.clearUserValues()

            solverFactory.createSolver().solve(grid, false) shouldBe 1
        }
    }

    context("3x4 grid") {
        withData(solverFactories) { solverFactory ->
            /*  |     3x  0 |     8+  1 |         1 |
                |         0 |         0 |         1 |
                |    12x  2 |         2 |         2 |
                |     9+  3 |         3 |         3 | */
            val builder = GridBuilder(3, 4)
            builder.addCage(3, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_TOP, 0)
                .addCage(8, GridCageAction.ACTION_ADD, GridCageType.ANGLE_LEFT_BOTTOM, 1)
                .addCage(12, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_HORIZONTAL, 6)
                .addCage(9, GridCageAction.ACTION_ADD, GridCageType.TRIPLE_HORIZONTAL, 9)
            val grid = builder.createGrid()
            println(grid.toString())
            grid.clearUserValues()

            solverFactory.createSolver().solve(grid, false) shouldBe 1
        }
    }
})
