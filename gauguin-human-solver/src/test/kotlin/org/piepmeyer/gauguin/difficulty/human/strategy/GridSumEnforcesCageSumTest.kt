package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCacheImpl

class GridSumEnforcesCageSumTest :
    FunSpec({

        test("4x4 grid") {
            val grid =
                GridBuilder(4, 4)
                    .addCageAdd(6, GridCageType.ANGLE_RIGHT_BOTTOM)
                    .addCageAdd(12, GridCageType.TETRIS_VERTICAL_RIGHT_TOP)
                    .addCageAdd(8, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM)
                    .addCageAdd(9, GridCageType.ANGLE_RIGHT_TOP)
                    .addCageSubtract(1, GridCageType.DOUBLE_HORIZONTAL)
                    .createGrid()

            grid.cells[0].possibles = setOf(1, 2, 3, 4)
            grid.cells[1].possibles = setOf(1, 2, 3, 4)
            grid.cells[2].possibles = setOf(1, 2, 3, 4)
            grid.cells[3].possibles = setOf(1, 2, 3, 4)
            grid.cells[4].possibles = setOf(1, 2, 3, 4)
            grid.cells[5].possibles = setOf(1, 2, 3, 4)
            grid.cells[6].possibles = setOf(1, 2, 3, 4)
            grid.cells[7].possibles = setOf(1, 2, 3, 4)
            grid.cells[8].possibles = setOf(1, 2, 3, 4)
            grid.cells[9].possibles = setOf(1, 2, 3, 4)
            grid.cells[10].possibles = setOf(1, 2, 3, 4)
            grid.cells[11].possibles = setOf(1, 2, 3, 4)
            grid.cells[12].possibles = setOf(1, 2, 3, 4)
            grid.cells[13].possibles = setOf(1, 2, 3, 4)
            grid.cells[14].possibles = setOf(1, 2, 3, 4)
            grid.cells[15].possibles = setOf(1, 2, 3, 4)

            val cache = HumanSolverCacheImpl.createValidatedCache(grid)

            val solver = GridSumEnforcesCageSum()

            println(grid)

            solver.fillCells(grid, cache).madeChanges() shouldBe true
            solver.fillCells(grid, cache).madeChanges() shouldBe false

            println(grid)

            assertSoftly {
                withClue("cell 14 got 1,4 removed as they must sum up to 5") {
                    grid.cells[14].possibles shouldContainExactly setOf(2, 3)
                }
                withClue("cell 15 got 1,4 removed as they must sum up to 5") {
                    grid.cells[15].possibles shouldContainExactly setOf(2, 3)
                }
            }
        }
    })
