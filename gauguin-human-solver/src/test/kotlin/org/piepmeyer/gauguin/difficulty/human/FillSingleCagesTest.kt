package org.piepmeyer.gauguin.difficulty.human

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.Grid

class FillSingleCagesTest :
    FunSpec({

        test("3x3 grid singles get filled") {
            val grid = createGrid()

            withClue("should fill two cells and return this number") {
                FillSingleCages().fillCells(grid) shouldBe 2
            }

            grid.cells[2].userValue shouldBe 3
            grid.cells[8].userValue shouldBe 1

            grid.cells.count { it.isUserValueSet } shouldBe 2
        }

        test("3x3 grid second run fills no cells") {
            val grid = createGrid()

            FillSingleCages().fillCells(grid)

            withClue("already filled single cells") {
                FillSingleCages().fillCells(grid) shouldBe 0
            }

            grid.cells[2].userValue shouldBe 3
            grid.cells[8].userValue shouldBe 1

            grid.cells.count { it.isUserValueSet } shouldBe 2
        }
    })

private fun createGrid(): Grid =
    GridBuilder(3, 3)
        .addCageDivide(3, GridCageType.DOUBLE_VERTICAL)
        .addCageMultiply(4, GridCageType.ANGLE_RIGHT_TOP)
        .addCageSingle(3)
        .addCageMultiply(6, GridCageType.DOUBLE_HORIZONTAL)
        .addCageSingle(1)
        .createGrid()
