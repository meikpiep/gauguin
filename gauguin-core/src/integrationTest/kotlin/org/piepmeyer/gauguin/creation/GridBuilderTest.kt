package org.piepmeyer.gauguin.creation

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.cage.GridCageType

class GridBuilderTest :
    FunSpec({
        test("3x3 grid as String") {
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

            grid.cells shouldHaveSize 9

            withClue("Cage 0") {
                grid.cages[0].cells shouldHaveSize 2
                grid.getValidCellAt(0, 0).cage().id shouldBe 0
                grid.getValidCellAt(1, 0).cage().id shouldBe 0
            }
            withClue("Cage 1") {
                grid.cages[1].cells shouldHaveSize 2
                grid.getValidCellAt(0, 1).cage().id shouldBe 1
                grid.getValidCellAt(0, 2).cage().id shouldBe 1
            }
            withClue("Cage 2") {
                grid.cages[2].cells shouldHaveSize 3
                grid.getValidCellAt(1, 1).cage().id shouldBe 2
                grid.getValidCellAt(1, 2).cage().id shouldBe 2
                grid.getValidCellAt(2, 2).cage().id shouldBe 2
            }
            withClue("Cage 3") {
                grid.cages[3].cells shouldHaveSize 2
                grid.getValidCellAt(2, 0).cage().id shouldBe 3
                grid.getValidCellAt(2, 1).cage().id shouldBe 3
            }
        }
    })
