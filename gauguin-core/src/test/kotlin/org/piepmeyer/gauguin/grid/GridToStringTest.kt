package org.piepmeyer.gauguin.grid

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType

class GridToStringTest : FunSpec({

    test("print 3x3 grid") {
        val grid =
            GridBuilder(3)
                .addCage(1, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_VERTICAL, 0)
                .addCage(3, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_HORIZONTAL, 1)
                .addCage(4, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_LEFT_BOTTOM, 4)
                .addCage(3, GridCageAction.ACTION_DIVIDE, GridCageType.DOUBLE_HORIZONTAL, 6)
                .createGrid()

        grid.getValidCellAt(0, 1).userValue = 3
        grid.getValidCellAt(0, 1).value = 1

        val expectedString =
            """
            Grid:
            |  -  - |  3  1 |  -  - |
            |  -  - |  -  - |  -  - |
            |  -  - |  -  - |  -  - |


            |     1-  0 |     3x  1 |         1 |
            |         0 |     4x  2 |         2 |
            |     3/  3 |         3 |         2 |
            """.trimIndent()

        GridToString(grid).printGrid() shouldBe expectedString
    }
})
