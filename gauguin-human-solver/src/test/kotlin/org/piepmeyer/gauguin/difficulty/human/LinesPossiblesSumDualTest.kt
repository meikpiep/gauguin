package org.piepmeyer.gauguin.difficulty.human

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.strategy.LinesSingleCagePossiblesSumDual

class LinesPossiblesSumDualTest :
    FunSpec({

        test("4x4 grid") {
            val grid =
                GridBuilder(4, 4)
                    .addCageMultiply(24, GridCageType.L_HORIZONTAL_SHORT_RIGHT_BOTTOM)
                    .addCageSingle(3)
                    .addCageSubtract(1, GridCageType.DOUBLE_VERTICAL)
                    .addCageAdd(9, GridCageType.ANGLE_RIGHT_TOP)
                    .addCageMultiply(8, GridCageType.TRIPLE_VERTICAL)
                    .addCageDivide(3, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageSingle(2)
                    .createGrid()

            grid.cells[0].possibles = setOf(2, 4)
            grid.cells[1].possibles = setOf(2, 4)
            grid.cells[2].userValue = 1
            grid.cells[3].userValue = 3
            grid.cells[4].possibles = setOf(1, 2, 4)
            grid.cells[5].possibles = setOf(2, 4)
            grid.cells[6].userValue = 3
            grid.cells[7].possibles = setOf(1, 2)
            grid.cells[8].possibles = setOf(1, 2, 3)
            grid.cells[9].possibles = setOf(1, 3)
            grid.cells[10].userValue = 4
            grid.cells[11].possibles = setOf(1, 2)
            grid.cells[12].possibles = setOf(1, 3)
            grid.cells[13].possibles = setOf(1, 3)
            grid.cells[14].userValue = 2
            grid.cells[15].userValue = 4

            val cache = HumanSolverCacheImpl.createValidatedCache(grid)

            val solver = LinesSingleCagePossiblesSumDual()

            println(grid)

            // solver should find two possibles and delete one of them for each run
            solver.fillCells(grid, cache).madeChanges() shouldBe true

            println(grid)

            assertSoftly {
                withClue("cell 4") {
                    grid.cells[4].possibles shouldContainExactly setOf(2)
                }
                withClue("cell 8") {
                    grid.cells[8].possibles shouldContainExactly setOf(3)
                }
            }
        }
    })
