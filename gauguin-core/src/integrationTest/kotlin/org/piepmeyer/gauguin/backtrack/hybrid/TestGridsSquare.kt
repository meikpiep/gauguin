package org.piepmeyer.gauguin.backtrack.hybrid

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.creation.dlx.MathDokuDLXSolver
import org.piepmeyer.gauguin.grid.GridCageAction
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant

class TestGridsSquare : FunSpec({
    context("2x2 grid") {
        /*  |     5+  0 |         0 |
            |     1   1 |         0 | */
        val builder = GridBuilder(2)
        builder.addCage(5, GridCageAction.ACTION_ADD, GridCageType.ANGLE_LEFT_BOTTOM, 0)
            .addCage(1, GridCageAction.ACTION_NONE, GridCageType.SINGLE, 2)
        val grid = builder.createGrid()
        println(grid.toString())
        grid.clearUserValues()

        MathDokuDLXSolver().solve(grid) shouldBe 1
    }

    context("3x3 grid 1") {
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

        MathDokuDLXSolver().solve(grid) shouldBe 1
    }

    context("3x3 grid 2") {
        /*  |     3x  0 |         0 |    12x  1 |
            |     5+  2 |         0 |         1 |
            |         2 |         1 |         1 | */
        val builder = GridBuilder(3)
        builder.addCage(3, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_LEFT_BOTTOM, 0)
            .addCage(
                12,
                GridCageAction.ACTION_MULTIPLY,
                GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM,
                2
            )
            .addCage(5, GridCageAction.ACTION_ADD, GridCageType.DOUBLE_VERTICAL, 3)
        val grid = builder.createGrid()
        println(grid.toString())
        grid.clearUserValues()

        MathDokuDLXSolver().solve(grid) shouldBe 1
    }

    context("3x3 grid 3") {
        /*  |     6+  0 |     7+  1 |         1 |
            |         0 |         1 |         1 |
            |         0 |     6x  2 |         2 | */
        val builder = GridBuilder(3)
        builder.addCage(6, GridCageAction.ACTION_ADD, GridCageType.TRIPLE_VERTICAL, 0)
            .addCage(7, GridCageAction.ACTION_ADD, GridCageType.SQUARE, 1)
            .addCage(6, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_HORIZONTAL, 7)
        val grid = builder.createGrid()
        println(grid.toString())
        grid.clearUserValues()

        MathDokuDLXSolver().solve(grid) shouldBe 2
    }

    context("4x4 grid 1") {
        /*  |     2/  0 |         0 |     3+  1 |         1 |
            |     0x  2 |     6+  3 |         3 |         3 |
            |         2 |         2 |     6+  4 |         3 |
            |     3-  5 |         5 |         4 |         4 |*/
        val builder = GridBuilder(4, DigitSetting.FIRST_DIGIT_ZERO)
        builder.addCage(2, GridCageAction.ACTION_DIVIDE, GridCageType.DOUBLE_HORIZONTAL, 0)
            .addCage(3, GridCageAction.ACTION_ADD, GridCageType.DOUBLE_HORIZONTAL, 2)
            .addCage(0, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_TOP, 4)
            .addCage(
                6,
                GridCageAction.ACTION_ADD,
                GridCageType.L_HORIZONTAL_SHORT_RIGHT_BOTTOM,
                5
            )
            .addCage(6, GridCageAction.ACTION_ADD, GridCageType.ANGLE_RIGHT_TOP, 10)
            .addCage(3, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_HORIZONTAL, 12)
        val grid = builder.createGrid()
        println(grid.toString())
        grid.clearUserValues()

        MathDokuDLXSolver().solve(grid) shouldBe 2
    }

    context("4x4 grid 2") {
        /*  |     6x  0 |     4+  1 |     2/  2 |     0x  3 |
            |         0 |         1 |         2 |         3 |
            |     0x  4 |         1 |         3 |         3 |
            |         4 |         4 |     3x  5 |         5 | */
        val builder = GridBuilder(4, DigitSetting.FIRST_DIGIT_ZERO)

        builder.addCage(6, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_VERTICAL, 0)
            .addCage(4, GridCageAction.ACTION_ADD, GridCageType.TRIPLE_VERTICAL, 1)
            .addCage(2, GridCageAction.ACTION_DIVIDE, GridCageType.DOUBLE_VERTICAL, 2)
            .addCage(
                0,
                GridCageAction.ACTION_MULTIPLY,
                GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM,
                3
            )
            .addCage(0, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_TOP, 8)
            .addCage(3, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_HORIZONTAL, 14)

        val grid = builder.createGrid()
        println(grid.toString())

        MathDokuDLXSolver().solve(grid) shouldBe 2
    }

    context("4x4 grid 3") {
        /*  |     1-  0 |     0x  1 |         1 |     6x  2 |
            |         0 |         1 |         2 |         2 |
            |     4+  3 |         3 |         3 |     3-  4 |
            |     5+  5 |         5 |         3 |         4 |*/
        val builder = GridBuilder(4, DigitSetting.FIRST_DIGIT_ZERO)
        builder.addCage(1, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_VERTICAL, 0)
            .addCage(0, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_BOTTOM, 1)
            .addCage(6, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_LEFT_TOP, 3)
            .addCage(
                4,
                GridCageAction.ACTION_ADD,
                GridCageType.L_HORIZONTAL_SHORT_RIGHT_BOTTOM,
                8
            )
            .addCage(3, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_VERTICAL, 11)
            .addCage(5, GridCageAction.ACTION_ADD, GridCageType.DOUBLE_HORIZONTAL, 12)
        val grid = builder.createGrid()
        println(grid.toString())

        MathDokuDLXSolver().solve(grid) shouldBe 2
    }

    context("4x4 grid 4") {
        /*  |    12x  0 |         0 |     1-  1 |     0x  2 |
            |         0 |     4+  3 |         1 |         2 |
            |         0 |         3 |    12x  4 |         2 |
            |         3 |         3 |         4 |         4 |*/
        val builder = GridBuilder(4, DigitSetting.FIRST_DIGIT_ZERO)
        builder.addCage(12, GridCageAction.ACTION_MULTIPLY, GridCageType.L_VERTICAL_SHORT_RIGHT_TOP, 0)
            .addCage(1, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_VERTICAL, 2)
            .addCage(0, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_VERTICAL, 3)
            .addCage(4, GridCageAction.ACTION_ADD, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM, 5)
            .addCage(12, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_TOP, 10)

        val grid = builder.createGrid()
        println(grid.toString())

        MathDokuDLXSolver().solve(grid) shouldBe 2
    }

    context("4x4 grid primes hidden operators") {
        /*  |      6  0 |     10  1 |         1 |         1 |
            |         0 |         0 |      4  2 |         2 |
            |     10  3 |         3 |         3 |      3  4 |
            |      1  5 |         5 |         3 |         4 |*/
        val variant = GameOptionsVariant.createClassic(DigitSetting.PRIME_NUMBERS)
        variant.showOperators = false

        val builder = GridBuilder(4, 4, variant)
        builder.addCage(6, GridCageAction.ACTION_NONE, GridCageType.ANGLE_RIGHT_TOP, 0)
            .addCage(10, GridCageAction.ACTION_NONE, GridCageType.TRIPLE_HORIZONTAL, 1)
            .addCage(4, GridCageAction.ACTION_NONE, GridCageType.DOUBLE_HORIZONTAL, 6)
            .addCage(10, GridCageAction.ACTION_NONE, GridCageType.L_HORIZONTAL_SHORT_RIGHT_BOTTOM, 8)
            .addCage(3, GridCageAction.ACTION_NONE, GridCageType.DOUBLE_VERTICAL, 11)
            .addCage(1, GridCageAction.ACTION_NONE, GridCageType.DOUBLE_HORIZONTAL, 12)

        val grid = builder.createGrid()
        println(grid.toString())

        MathDokuDLXSolver().solve(grid) shouldBe 2
    }

    context("5x5 grid Fibonacci") {
        /*  |    80x  0 |         0 |         0 |    15x  1 |         1 |
            |         0 |    30x  2 |     1-  3 |         3 |    17+  4 |
            |     2   5 |         2 |     8/  6 |         6 |         4 |
            |         2 |         2 |     3-  7 |         4 |         4 |
            |     5-  8 |         8 |         7 |     2x  9 |         9 |*/
        val builder = GridBuilder(5, DigitSetting.FIBONACCI_SEQUENCE)
        builder.addCage(80, GridCageAction.ACTION_MULTIPLY, GridCageType.L_HORIZONTAL_SHORT_LEFT_BOTTOM, 0)
            .addCage(15, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_HORIZONTAL, 3)
            .addCage(30, GridCageAction.ACTION_MULTIPLY, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM, 6)
            .addCage(1, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_HORIZONTAL, 7)
            .addCage(17, GridCageAction.ACTION_ADD, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM, 9)
            .addCage(2, GridCageAction.ACTION_NONE, GridCageType.SINGLE, 10)
            .addCage(8, GridCageAction.ACTION_DIVIDE, GridCageType.DOUBLE_HORIZONTAL, 12)
            .addCage(3, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_VERTICAL, 17)
            .addCage(5, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_HORIZONTAL, 20)
            .addCage(2, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_HORIZONTAL, 23)

        val grid = builder.createGrid()
        println(grid.toString())

        MathDokuDLXSolver().solve(grid) shouldBe 1
    }
})
