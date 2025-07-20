package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCacheImpl
import org.piepmeyer.gauguin.grid.GridCageAction

class NakedTripleTest :
    FunSpec({

        test("naked triple with all three possibles in each cell") {
            val grid =
                GridBuilder(5, 1)
                    .addSingleCage(4, 3)
                    .addSingleCage(5, 4)
                    .addCage(
                        6,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.TRIPLE_HORIZONTAL,
                        0,
                    ).createGrid()

            grid.cells[0].possibles = setOf(1, 2, 3)
            grid.cells[1].possibles = setOf(1, 2, 3)
            grid.cells[2].possibles = setOf(1, 2, 3)
            grid.cells[3].possibles = setOf(1, 2, 3, 4, 5)
            grid.cells[4].possibles = setOf(1, 2, 3, 4, 5)

            val solver = NakedTriple()

            println(grid)

            // solver should find two possibles and delete one of them for each run
            solver.fillCells(grid, HumanSolverCacheImpl(grid)).first shouldBe true

            println(grid)

            assertSoftly {
                withClue("possibles should be deleted from other cells") {
                    grid.cells[3].possibles shouldContainExactly setOf(4, 5)
                    grid.cells[4].possibles shouldContainExactly setOf(4, 5)
                }
            }
        }

        test("naked triple with different possible combination in each cell") {
            val grid =
                GridBuilder(5, 1)
                    .addSingleCage(4, 3)
                    .addSingleCage(5, 4)
                    .addCage(
                        6,
                        GridCageAction.ACTION_MULTIPLY,
                        GridCageType.TRIPLE_HORIZONTAL,
                        0,
                    ).createGrid()

            grid.cells[0].possibles = setOf(1, 2)
            grid.cells[1].possibles = setOf(2, 3)
            grid.cells[2].possibles = setOf(1, 3)
            grid.cells[3].possibles = setOf(1, 2, 3, 4, 5)
            grid.cells[4].possibles = setOf(1, 2, 3, 4, 5)

            val solver = NakedTriple()

            println(grid)

            // solver should find two possibles and delete one of them for each run
            solver.fillCells(grid, HumanSolverCacheImpl(grid)).first shouldBe true

            println(grid)

            assertSoftly {
                withClue("possibles should be deleted from other cells") {
                    grid.cells[3].possibles shouldContainExactly setOf(4, 5)
                    grid.cells[4].possibles shouldContainExactly setOf(4, 5)
                }
            }
        }
    })
