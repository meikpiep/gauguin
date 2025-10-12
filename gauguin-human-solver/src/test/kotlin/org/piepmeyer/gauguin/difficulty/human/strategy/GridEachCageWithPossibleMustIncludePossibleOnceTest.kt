package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCacheImpl

class GridEachCageWithPossibleMustIncludePossibleOnceTest :
    FunSpec({

        test("4x3 grid") {
            val grid =
                GridBuilder(4, 3)
                    .addCageDivide(4, GridCageType.DOUBLE_VERTICAL)
                    .addCageMultiply(48, GridCageType.TETRIS_VERTICAL_LEFT_TOP)
                    .addCageSubtract(1, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageSubtract(2, GridCageType.DOUBLE_VERTICAL)
                    .addCageDivide(2, GridCageType.DOUBLE_HORIZONTAL)
                    .createGrid()

            grid.cells[0].possibles = setOf(1, 4)
            grid.cells[1].possibles = setOf(2, 3, 4)
            grid.cells[2].possibles = setOf(1, 2, 3, 4)
            grid.cells[3].possibles = setOf(1, 2, 3, 4)
            grid.cells[4].possibles = setOf(1, 4)
            grid.cells[5].possibles = setOf(1, 3, 4)
            grid.cells[6].possibles = setOf(1, 2, 3, 4)
            grid.cells[7].possibles = setOf(1, 2, 3)
            grid.cells[8].userValue = 2
            grid.cells[9].possibles = setOf(1, 4)
            grid.cells[10].possibles = setOf(1, 3, 4)
            grid.cells[11].possibles = setOf(1, 3, 4)

            val solver = GridEachCageWithPossibleMustIncludePossibleOnce()
            val cache = HumanSolverCacheImpl(grid)
            cache.initialize()

            println(grid)

            solver.fillCells(grid, cache).madeChanges() shouldBe true

            println(grid)

            assertSoftly {
                withClue("possibles containing no 3 should be deleted from relevant cells") {
                    grid.cells[2].possibles shouldContainExactly setOf(2, 3, 4)
                    grid.cells[3].possibles shouldContainExactly setOf(2, 3, 4)
                    grid.cells[7].possibles shouldContainExactly setOf(1, 3)
                    grid.cells[11].possibles shouldContainExactly setOf(1, 3)
                }
            }
        }
    })
