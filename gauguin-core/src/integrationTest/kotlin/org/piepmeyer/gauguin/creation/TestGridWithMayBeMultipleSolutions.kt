package org.piepmeyer.gauguin.creation

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.creation.dlx.DLX
import org.piepmeyer.gauguin.creation.dlx.MathDokuDLX

private val logger = KotlinLogging.logger {}

class TestGridWithMayBeMultipleSolutions :
    FunSpec({
        test("test") {
            val builder = GridBuilder(9, 9)

            builder.addCageMultiply(6, GridCageType.ANGLE_LEFT_BOTTOM)
            builder.addCageSingle(6)
            builder.addCageAdd(16, GridCageType.ANGLE_LEFT_TOP)
            builder.addCageMultiply(40, GridCageType.TRIPLE_HORIZONTAL)
            builder.addCageAdd(25, GridCageType.L_VERTICAL_SHORT_RIGHT_TOP)
            builder.addCageSubtract(5, GridCageType.DOUBLE_VERTICAL)
            builder.addCageSingle(4)
            builder.addCageSubtract(8, GridCageType.DOUBLE_HORIZONTAL)
            builder.addCageAdd(23, GridCageType.TRIPLE_VERTICAL)
            builder.addCageSubtract(4, GridCageType.DOUBLE_HORIZONTAL)
            builder.addCageAdd(11, GridCageType.L_VERTICAL_SHORT_RIGHT_TOP)
            builder.addCageSubtract(3, GridCageType.DOUBLE_HORIZONTAL)
            builder.addCageSubtract(2, GridCageType.DOUBLE_HORIZONTAL)
            builder.addCageMultiply(70, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM)
            builder.addCageMultiply(168, GridCageType.ANGLE_RIGHT_TOP)
            builder.addCageSingle(5)
            builder.addCageMultiply(48, GridCageType.DOUBLE_HORIZONTAL)
            builder.addCageSubtract(2, GridCageType.DOUBLE_HORIZONTAL)
            builder.addCageMultiply(45, GridCageType.ANGLE_LEFT_BOTTOM)
            builder.addCageMultiply(24, GridCageType.TRIPLE_VERTICAL)
            builder.addCageMultiply(210, GridCageType.TRIPLE_VERTICAL)
            builder.addCageMultiply(216, GridCageType.TRIPLE_HORIZONTAL)
            builder.addCageAdd(18, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM)
            builder.addCageAdd(18, GridCageType.ANGLE_RIGHT_BOTTOM)
            builder.addCageSingle(7)
            builder.addCageAdd(21, GridCageType.ANGLE_RIGHT_BOTTOM)
            builder.addCageMultiply(216, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM)
            builder.addCageAdd(10, GridCageType.ANGLE_LEFT_BOTTOM)
            builder.addCageMultiply(5, GridCageType.DOUBLE_VERTICAL)
            builder.addCageMultiply(504, GridCageType.TRIPLE_HORIZONTAL)
            builder.addCageAdd(7, GridCageType.DOUBLE_HORIZONTAL)

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

            logger.debug { grid }

            mdd.solve(DLX.SolveType.MULTIPLE) shouldBe 1
        }
    })
