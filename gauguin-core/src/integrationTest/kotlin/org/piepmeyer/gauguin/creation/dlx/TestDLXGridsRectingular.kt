package org.piepmeyer.gauguin.creation.dlx

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.options.DigitSetting

private val logger = KotlinLogging.logger {}

class TestDLXGridsRectingular :
    FunSpec({

        context("2x3 grid") {
        /*  |    12x  0 |     1   1 |
            |         0 |         0 |
            |     3x  2 |         2 | */
            val builder = GridBuilder(2, 3)
            builder
                .addCageMultiply(12, GridCageType.ANGLE_RIGHT_TOP)
                .addCageSingle(1)
                .addCageMultiply(3, GridCageType.DOUBLE_HORIZONTAL)
            val grid = builder.createGrid()

            logger.debug { grid }

            grid.clearUserValues()

            MathDokuDLXSolver().solve(grid) shouldBe 1
        }

        context("2x3 grid with zero") {
        /*  |     2x  0 |         0 |
            |         0 |    2+   1 |
            |         1 |         1 | */
            val builder = GridBuilder(2, 3, DigitSetting.FIRST_DIGIT_ZERO)
            builder
                .addCageMultiply(2, GridCageType.ANGLE_RIGHT_BOTTOM)
                .addCageAdd(2, GridCageType.ANGLE_LEFT_TOP)
            val grid = builder.createGrid()

            logger.debug { grid }
            grid.clearUserValues()

            MathDokuDLXSolver().solve(grid) shouldBe 1
        }

        context("2x3 grid with digits from -5 on") {
        /*  |   -48x  0 |         0 |
            |    15x  1 |         0 |
            |         1 |    5-   2 | */
            val builder = GridBuilder(2, 3, DigitSetting.FIRST_DIGIT_MINUS_FIVE)
            builder
                .addCageMultiply(-48, GridCageType.ANGLE_LEFT_BOTTOM)
                .addCageMultiply(15, GridCageType.DOUBLE_VERTICAL)
                .addCageSingle(-5)
            val grid = builder.createGrid()

            logger.debug { grid }
            grid.clearUserValues()

            MathDokuDLXSolver().solve(grid) shouldBe 1
        }

        context("3x2 grid") {
        /*  |     1   0 |   12x   1 |    3x   2 |
            |         1 |         1 |     1   2 | */
            val builder = GridBuilder(3, 2)
            builder
                .addCageSingle(1)
                .addCageMultiply(12, GridCageType.ANGLE_LEFT_TOP)
                .addCageMultiply(3, GridCageType.DOUBLE_VERTICAL)
            val grid = builder.createGrid()

            logger.debug { grid }
            grid.clearUserValues()

            MathDokuDLXSolver().solve(grid) shouldBe 1
        }

        context("2x4 grid") {
        /*  |     4x  0 |         0 |
            |         0 |     8+  1 |
            |         1 |         1 |
            |     2/  2 |         2 | */
            val builder = GridBuilder(2, 4)
            builder
                .addCageMultiply(4, GridCageType.ANGLE_RIGHT_BOTTOM)
                .addCageAdd(8, GridCageType.ANGLE_LEFT_TOP)
                .addCageDivide(2, GridCageType.DOUBLE_HORIZONTAL)
            val grid = builder.createGrid()

            logger.debug { grid }
            grid.clearUserValues()

            MathDokuDLXSolver().solve(grid) shouldBe 1
        }

        context("2x6 grid") {
        /*  |     8x  0 |     2   1 |
            |         0 |         0 |
            |     5-  2 |         2 |
            |    75x  3 |     6   4 |
            |         3 |         3 |
            |    12x  5 |         5 | */
            val builder = GridBuilder(2, 6)
            builder
                .addCageMultiply(8, GridCageType.ANGLE_RIGHT_TOP)
                .addCageSingle(2)
                .addCageSubtract(5, GridCageType.DOUBLE_HORIZONTAL)
                .addCageMultiply(75, GridCageType.ANGLE_RIGHT_TOP)
                .addCageSingle(6)
                .addCageMultiply(12, GridCageType.DOUBLE_HORIZONTAL)
            val grid = builder.createGrid()

            logger.debug { grid }
            grid.clearUserValues()

            MathDokuDLXSolver().solve(grid) shouldBe 1
        }

        context("3x4 grid") {
        /*  |     3x  0 |     8+  1 |         1 |
            |         0 |         0 |         1 |
            |    12x  2 |         2 |         2 |
            |     9+  3 |         3 |         3 | */
            val builder = GridBuilder(3, 4)
            builder
                .addCageMultiply(3, GridCageType.ANGLE_RIGHT_TOP)
                .addCageAdd(8, GridCageType.ANGLE_LEFT_BOTTOM)
                .addCageMultiply(12, GridCageType.TRIPLE_HORIZONTAL)
                .addCageAdd(9, GridCageType.TRIPLE_HORIZONTAL)
            val grid = builder.createGrid()

            logger.debug { grid }
            grid.clearUserValues()

            MathDokuDLXSolver().solve(grid) shouldBe 1
        }
    })
