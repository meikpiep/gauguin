package org.piepmeyer.gauguin.creation

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.creation.dlx.DLX
import org.piepmeyer.gauguin.creation.dlx.MathDokuDLX

private val logger = KotlinLogging.logger {}

class TestSmallGridCreation :
    FunSpec({
        test("firstGrid3x3") {
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
            val mdd = MathDokuDLX(grid)

            logger.debug { grid }

            grid.getCell(8).cage().id shouldBe 2

            mdd.solve(DLX.SolveType.MULTIPLE) shouldBe 1
        }
    })
