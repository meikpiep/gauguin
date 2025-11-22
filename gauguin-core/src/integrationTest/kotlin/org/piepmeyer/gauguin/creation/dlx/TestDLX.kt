package org.piepmeyer.gauguin.creation.dlx

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType

class TestDLX :
    FunSpec({
        test("firstGrid3x3") {
        /*  |     1-  0 |     3x  1 |         1 |
		    |         0 |     4x  2 |         2 |
    		|     3/  3 |         3 |         2 | */
            val grid =
                GridBuilder(3)
                    .addCageSubtract(1, GridCageType.DOUBLE_VERTICAL)
                    .addCageMultiply(3, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageMultiply(4, GridCageType.ANGLE_LEFT_BOTTOM)
                    .addCageDivide(3, GridCageType.DOUBLE_HORIZONTAL)
                    .createGrid()

            MathDokuDLX(grid).solve(DLX.SolveType.MULTIPLE) shouldBe 1
        }
    })
