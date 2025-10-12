package org.piepmeyer.gauguin.grid

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType

class GridToStringTest :
    FunSpec({

        test("print 3x3 grid") {
            val grid =
                GridBuilder(3)
                    .addCageSubtract(1, GridCageType.DOUBLE_VERTICAL)
                    .addCageMultiply(3, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageMultiply(4, GridCageType.ANGLE_LEFT_BOTTOM)
                    .addCageDivide(3, GridCageType.DOUBLE_HORIZONTAL)
                    .createGrid()

            grid.getValidCellAt(0, 1).userValue = 3
            grid.getValidCellAt(0, 1).value = 1
            grid.getValidCellAt(2, 2).addPossible(1)
            grid.getValidCellAt(2, 2).addPossible(2)

            val expectedString =
                """
                Grid:
                |  -  - |  3  1 |  -  - |
                |  -  - |  -  - |  -  - |
                |  -  - |  -  - |  -  - |


                |     1-  0     [] |     3x  1      3 |         1     [] |
                |         0     [] |     4x  2     [] |         2     [] |
                |     3/  3     [] |         3     [] |         2 [1, 2] |
                """.trimIndent()

            GridToString(grid).printGrid() shouldBe expectedString
        }
    })
