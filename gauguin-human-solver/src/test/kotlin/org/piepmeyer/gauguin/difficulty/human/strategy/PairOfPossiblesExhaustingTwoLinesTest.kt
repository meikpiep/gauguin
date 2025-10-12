package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType

class PairOfPossiblesExhaustingTwoLinesTest :
    FunSpec({

        test("2x6 detects 3 and 4 first step") {
            val grid =
                GridBuilder(2, 6)
                    .addCageSubtract(2, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageSubtract(1, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageMultiply(18, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageSingle(2)
                    .addCageSingle(5)
                    .addCageDivide(3, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageAdd(9, GridCageType.DOUBLE_HORIZONTAL)
                    .createGrid()

            grid.cells[0].possibles = setOf(1, 3, 4, 5, 6)
            grid.cells[1].possibles = setOf(1, 2, 3, 4, 6)
            grid.cells[2].possibles = setOf(1, 3, 4, 5)
            grid.cells[3].possibles = setOf(2, 3, 4)
            grid.cells[4].possibles = setOf(3, 6)
            grid.cells[5].possibles = setOf(3, 6)
            grid.cells[6].userValue = 2
            grid.cells[7].userValue = 5
            grid.cells[8].possibles = setOf(1, 3, 6)
            grid.cells[9].possibles = setOf(1, 2, 3)
            grid.cells[10].possibles = setOf(3, 5, 6)
            grid.cells[11].possibles = setOf(3, 4, 6)

            println(grid)

            val solver = PairOfPossiblesExhaustingTwoLines()
            solver.fillCellsWithNewCache(grid) shouldBe true

            println(grid)

            assertSoftly {
                withClue("possibles should be deleted from other cells") {
                    grid.cells[0].possibles shouldContainExactly setOf(1, 3, 4, 5, 6)
                    grid.cells[1].possibles shouldContainExactly setOf(1, 2, 3, 4, 6)
                    grid.cells[2].possibles shouldContainExactly setOf(1, 3, 5)
                    grid.cells[3].possibles shouldContainExactly setOf(2, 4)
                    grid.cells[4].possibles shouldContainExactly setOf(3, 6)
                    grid.cells[5].possibles shouldContainExactly setOf(3, 6)
                    grid.cells[8].possibles shouldContainExactly setOf(1, 3, 6)
                    grid.cells[9].possibles shouldContainExactly setOf(1, 2, 3)
                    grid.cells[10].possibles shouldContainExactly setOf(3, 5, 6)
                    grid.cells[11].possibles shouldContainExactly setOf(3, 4, 6)
                }
            }
        }

        test("2x6 detects 3 and 6 last step") {
            val grid =
                GridBuilder(2, 6)
                    .addCageSubtract(2, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageSubtract(1, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageMultiply(18, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageSingle(2)
                    .addCageSingle(5)
                    .addCageDivide(3, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageAdd(9, GridCageType.DOUBLE_HORIZONTAL)
                    .createGrid()

            grid.cells[0].possibles = setOf(1, 3, 4, 5, 6)
            grid.cells[1].possibles = setOf(1, 2, 3, 4, 6)
            grid.cells[2].possibles = setOf(1, 3, 5)
            grid.cells[3].possibles = setOf(2, 4)
            grid.cells[4].possibles = setOf(3, 6)
            grid.cells[5].possibles = setOf(3, 6)
            grid.cells[6].userValue = 2
            grid.cells[7].userValue = 5
            grid.cells[8].possibles = setOf(1, 3, 6)
            grid.cells[9].possibles = setOf(1, 2, 3)
            grid.cells[10].possibles = setOf(3, 5, 6)
            grid.cells[11].possibles = setOf(3, 4, 6)

            println(grid)

            val solver = PairOfPossiblesExhaustingTwoLines()
            solver.fillCellsWithNewCache(grid) shouldBe true
            solver.fillCellsWithNewCache(grid) shouldBe false

            println(grid)

            assertSoftly {
                withClue("possibles should be deleted from other cells") {
                    grid.cells[0].possibles shouldContainExactly setOf(1, 3, 4, 5, 6)
                    grid.cells[1].possibles shouldContainExactly setOf(1, 2, 3, 4, 6)
                    grid.cells[2].possibles shouldContainExactly setOf(1, 3, 5)
                    grid.cells[3].possibles shouldContainExactly setOf(2, 4)
                    grid.cells[8].possibles shouldContainExactly setOf(1, 3, 6)
                    grid.cells[9].possibles shouldContainExactly setOf(1, 2, 3)
                    grid.cells[10].possibles shouldContainExactly setOf(5)
                    grid.cells[11].possibles shouldContainExactly setOf(4)
                }
            }
        }
    })
