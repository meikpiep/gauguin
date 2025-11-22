package org.piepmeyer.gauguin.creation.dlx

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.GridCageAction
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant

private val logger = KotlinLogging.logger {}

class TestDLXGridsSquare :
    FunSpec({
        context("2x2 grid") {
        /*  |     5+  0 |         0 |
            |     1   1 |         0 | */
            val builder = GridBuilder(2)
            builder
                .addCageAdd(5, GridCageType.ANGLE_LEFT_BOTTOM)
                .addCageSingle(1)
            val grid = builder.createGrid()

            logger.debug { grid }
            grid.clearUserValues()

            MathDokuDLXSolver().solve(grid) shouldBe 1
        }

        context("3x3 grid 1") {
        /*  |     1-  0 |     3x  1 |         1 |
            |         0 |     4x  2 |         2 |
            |     3/  3 |         3 |         2 | */
            val builder = GridBuilder(3)
            builder
                .addCageSubtract(1, GridCageType.DOUBLE_VERTICAL)
                .addCageMultiply(3, GridCageType.DOUBLE_HORIZONTAL)
                .addCageMultiply(4, GridCageType.ANGLE_LEFT_BOTTOM)
                .addCageDivide(3, GridCageType.DOUBLE_HORIZONTAL)
            val grid = builder.createGrid()

            logger.debug { grid }
            grid.clearUserValues()

            MathDokuDLXSolver().solve(grid) shouldBe 1
        }

        context("3x3 grid 2") {
        /*  |     3x  0 |         0 |    12x  1 |
            |     5+  2 |         0 |         1 |
            |         2 |         1 |         1 | */
            val builder = GridBuilder(3)
            builder
                .addCageMultiply(3, GridCageType.ANGLE_LEFT_BOTTOM)
                .addCageMultiply(12, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM)
                .addCageAdd(5, GridCageType.DOUBLE_VERTICAL)
            val grid = builder.createGrid()

            logger.debug { grid }
            grid.clearUserValues()

            MathDokuDLXSolver().solve(grid) shouldBe 1
        }

        context("3x3 grid 3") {
        /*  |     6+  0 |     7+  1 |         1 |
            |         0 |         1 |         1 |
            |         0 |     6x  2 |         2 | */
            val builder = GridBuilder(3)
            builder
                .addCageAdd(6, GridCageType.TRIPLE_VERTICAL)
                .addCageAdd(7, GridCageType.SQUARE)
                .addCageMultiply(6, GridCageType.DOUBLE_HORIZONTAL)
            val grid = builder.createGrid()

            logger.debug { grid }
            grid.clearUserValues()

            MathDokuDLXSolver().solve(grid) shouldBe 2
        }

        context("4x4 grid 1") {
        /*  |     2/  0 |         0 |     3+  1 |         1 |
            |     0x  2 |     6+  3 |         3 |         3 |
            |         2 |         2 |     6+  4 |         3 |
            |     3-  5 |         5 |         4 |         4 |*/
            val builder = GridBuilder(4, DigitSetting.FIRST_DIGIT_ZERO)
            builder
                .addCageDivide(2, GridCageType.DOUBLE_HORIZONTAL)
                .addCageAdd(3, GridCageType.DOUBLE_HORIZONTAL)
                .addCageMultiply(0, GridCageType.ANGLE_RIGHT_TOP)
                .addCageAdd(6, GridCageType.L_HORIZONTAL_SHORT_RIGHT_BOTTOM)
                .addCageAdd(6, GridCageType.ANGLE_RIGHT_TOP)
                .addCageSubtract(3, GridCageType.DOUBLE_HORIZONTAL)
            val grid = builder.createGrid()

            logger.debug { grid }
            grid.clearUserValues()

            MathDokuDLXSolver().solve(grid) shouldBe 2
        }

        context("4x4 grid 2") {
        /*  |     6x  0 |     4+  1 |     2/  2 |     0x  3 |
            |         0 |         1 |         2 |         3 |
            |     0x  4 |         1 |         3 |         3 |
            |         4 |         4 |     3x  5 |         5 | */
            val builder = GridBuilder(4, DigitSetting.FIRST_DIGIT_ZERO)

            builder
                .addCageMultiply(6, GridCageType.DOUBLE_VERTICAL)
                .addCageAdd(4, GridCageType.TRIPLE_VERTICAL)
                .addCageDivide(2, GridCageType.DOUBLE_VERTICAL)
                .addCageMultiply(0, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM)
                .addCageMultiply(0, GridCageType.ANGLE_RIGHT_TOP)
                .addCageMultiply(3, GridCageType.DOUBLE_HORIZONTAL)

            val grid = builder.createGrid()
            logger.debug { grid }

            MathDokuDLXSolver().solve(grid) shouldBe 2
        }

        context("4x4 grid 3") {
        /*  |     1-  0 |     0x  1 |         1 |     6x  2 |
            |         0 |         1 |         2 |         2 |
            |     4+  3 |         3 |         3 |     3-  4 |
            |     5+  5 |         5 |         3 |         4 |*/
            val builder = GridBuilder(4, DigitSetting.FIRST_DIGIT_ZERO)
            builder
                .addCageSubtract(1, GridCageType.DOUBLE_VERTICAL)
                .addCageMultiply(0, GridCageType.ANGLE_RIGHT_BOTTOM)
                .addCageMultiply(6, GridCageType.ANGLE_LEFT_TOP)
                .addCageAdd(4, GridCageType.L_HORIZONTAL_SHORT_RIGHT_BOTTOM)
                .addCageSubtract(3, GridCageType.DOUBLE_VERTICAL)
                .addCageAdd(5, GridCageType.DOUBLE_HORIZONTAL)
            val grid = builder.createGrid()
            logger.debug { grid }

            MathDokuDLXSolver().solve(grid) shouldBe 2
        }

        context("4x4 grid 4") {
        /*  |    12x  0 |         0 |     1-  1 |     0x  2 |
            |         0 |     4+  3 |         1 |         2 |
            |         0 |         3 |    12x  4 |         2 |
            |         3 |         3 |         4 |         4 |*/
            val builder = GridBuilder(4, DigitSetting.FIRST_DIGIT_ZERO)
            builder
                .addCageMultiply(12, GridCageType.L_VERTICAL_SHORT_RIGHT_TOP)
                .addCageSubtract(1, GridCageType.DOUBLE_VERTICAL)
                .addCageMultiply(0, GridCageType.TRIPLE_VERTICAL)
                .addCageAdd(4, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM)
                .addCageMultiply(12, GridCageType.ANGLE_RIGHT_TOP)

            val grid = builder.createGrid()
            logger.debug { grid }

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
            builder
                .addCage(6, GridCageAction.ACTION_NONE, GridCageType.ANGLE_RIGHT_TOP)
                .addCage(10, GridCageAction.ACTION_NONE, GridCageType.TRIPLE_HORIZONTAL)
                .addCage(4, GridCageAction.ACTION_NONE, GridCageType.DOUBLE_HORIZONTAL)
                .addCage(10, GridCageAction.ACTION_NONE, GridCageType.L_HORIZONTAL_SHORT_RIGHT_BOTTOM)
                .addCage(3, GridCageAction.ACTION_NONE, GridCageType.DOUBLE_VERTICAL)
                .addCage(1, GridCageAction.ACTION_NONE, GridCageType.DOUBLE_HORIZONTAL)

            val grid = builder.createGrid()
            logger.debug { grid }

            MathDokuDLXSolver().solve(grid) shouldBe 2
        }

        context("5x5 grid Fibonacci") {
        /*  |    80x  0 |         0 |         0 |    15x  1 |         1 |
            |         0 |    30x  2 |     1-  3 |         3 |    17+  4 |
            |     2   5 |         2 |     8/  6 |         6 |         4 |
            |         2 |         2 |     3-  7 |         4 |         4 |
            |     5-  8 |         8 |         7 |     2x  9 |         9 |*/
            val builder = GridBuilder(5, DigitSetting.FIBONACCI_SEQUENCE)
            builder
                .addCageMultiply(80, GridCageType.L_HORIZONTAL_SHORT_LEFT_BOTTOM)
                .addCageMultiply(15, GridCageType.DOUBLE_HORIZONTAL)
                .addCageMultiply(30, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM)
                .addCageSubtract(1, GridCageType.DOUBLE_HORIZONTAL)
                .addCageAdd(17, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM)
                .addCageSingle(2)
                .addCageDivide(8, GridCageType.DOUBLE_HORIZONTAL)
                .addCageSubtract(3, GridCageType.DOUBLE_VERTICAL)
                .addCageSubtract(5, GridCageType.DOUBLE_HORIZONTAL)
                .addCageMultiply(2, GridCageType.DOUBLE_HORIZONTAL)

            val grid = builder.createGrid()
            logger.debug { grid }

            MathDokuDLXSolver().solve(grid) shouldBe 1
        }
    })
