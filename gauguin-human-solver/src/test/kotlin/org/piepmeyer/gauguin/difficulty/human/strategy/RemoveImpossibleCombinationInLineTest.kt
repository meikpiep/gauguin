package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType

class RemoveImpossibleCombinationInLineTest :
    FunSpec({

        test("4x4 grid") {
            val grid =
                GridBuilder(4, 4)
                    .addCageMultiply(24, GridCageType.TRIPLE_HORIZONTAL)
                    .addCageSingle(1)
                    .addCageMultiply(24, GridCageType.L_VERTICAL_SHORT_RIGHT_TOP)
                    .addCageAdd(8, GridCageType.ANGLE_LEFT_BOTTOM)
                    .addCageDivide(2, GridCageType.DOUBLE_VERTICAL)
                    .addCageSingle(4)
                    .addCageSubtract(1, GridCageType.DOUBLE_HORIZONTAL)
                    .createGrid()

            grid.cells[0].possibles = setOf(3, 4)
            grid.cells[1].possibles = setOf(3, 4)
            grid.cells[2].userValue = 2
            grid.cells[3].userValue = 1
            grid.cells[4].possibles = setOf(1, 2, 3, 4)
            grid.cells[5].possibles = setOf(3, 4)
            grid.cells[6].possibles = setOf(1, 3)
            grid.cells[7].possibles = setOf(2, 4)
            grid.cells[8].possibles = setOf(1, 2)
            grid.cells[9].possibles = setOf(1, 2)
            grid.cells[10].userValue = 4
            grid.cells[11].userValue = 3
            grid.cells[12].possibles = setOf(1, 2, 3, 4)
            grid.cells[13].possibles = setOf(1, 2)
            grid.cells[14].possibles = setOf(1, 3)
            grid.cells[15].possibles = setOf(2, 4)

            val solver = RemoveImpossibleCombinationInLine()

            println(grid)

            // solver should find two possibles and delete one of them for each run
            solver.fillCellsWithNewCache(grid) shouldBe true

            println(grid)

            assertSoftly {
                withClue("cell 14") {
                    grid.cells[14].possibles shouldContainExactly setOf(3)
                }
            }
        }

        test("4x4 grid impossible combinations") {
            val grid =
                GridBuilder(4, 4)
                    .addCageDivide(3, GridCageType.DOUBLE_VERTICAL)
                    .addCageAdd(9, GridCageType.ANGLE_LEFT_BOTTOM)
                    .addCageSingle(2)
                    .addCageMultiply(6, GridCageType.DOUBLE_VERTICAL)
                    .addCageAdd(8, GridCageType.TRIPLE_VERTICAL)
                    .addCageDivide(2, GridCageType.DOUBLE_VERTICAL)
                    .addCageSubtract(1, GridCageType.DOUBLE_VERTICAL)
                    .addCageSingle(1)
                    .createGrid()

            grid.cells[0].possibles = setOf(1, 3)
            grid.cells[1].userValue = 4
            grid.cells[2].possibles = setOf(1, 3)
            grid.cells[3].userValue = 2
            grid.cells[4].possibles = setOf(1, 3)
            grid.cells[5].possibles = setOf(2, 3)
            grid.cells[6].possibles = setOf(2, 4)
            grid.cells[7].possibles = setOf(1, 3, 4)
            grid.cells[8].possibles = setOf(2, 4)
            grid.cells[9].possibles = setOf(2, 3)
            grid.cells[10].possibles = setOf(1, 2, 3, 4)
            grid.cells[11].possibles = setOf(1, 3, 4)
            grid.cells[12].possibles = setOf(2, 4)
            grid.cells[13].userValue = 1
            grid.cells[14].possibles = setOf(2, 3, 4)
            grid.cells[15].possibles = setOf(3, 4)

            val solver = RemoveImpossibleCombinationInLine()

            println(grid)

            // solver should find two possibles and delete them in column three
            solver.fillCellsWithNewCache(grid) shouldBe true
            solver.fillCellsWithNewCache(grid) shouldBe true

            solver.fillCellsWithNewCache(grid) shouldBe true
            solver.fillCellsWithNewCache(grid) shouldBe true
            solver.fillCellsWithNewCache(grid) shouldBe true
            solver.fillCellsWithNewCache(grid) shouldBe true

            solver.fillCellsWithNewCache(grid) shouldBe false

            println(grid)

            assertSoftly {
                withClue("cell 10") {
                    grid.cells[10].possibles shouldContainExactly setOf(2, 3)
                }
                withClue("cell 14") {
                    grid.cells[14].possibles shouldContainExactly setOf(2, 3)
                }
            }
        }
    })
