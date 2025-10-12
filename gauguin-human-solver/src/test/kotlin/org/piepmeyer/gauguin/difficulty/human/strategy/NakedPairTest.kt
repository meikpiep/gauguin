package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.difficulty.human.HumanSolverCacheImpl

class NakedPairTest :
    FunSpec({

        test("4x1 grid") {
            val grid =
                GridBuilder(4, 1)
                    .addCageMultiply(3, GridCageType.DOUBLE_HORIZONTAL)
                    .addCageSingle(2)
                    .addCageSingle(4)
                    .createGrid()

            grid.cells[0].possibles = setOf(1, 3)
            grid.cells[1].possibles = setOf(1, 3)
            grid.cells[2].possibles = setOf(1, 2, 3, 4)
            grid.cells[3].possibles = setOf(1, 2, 3, 4)

            val solver = NakedPair()

            println(grid)

            // solver should find two possibles and delete one of them for each run
            solver.fillCells(grid, HumanSolverCacheImpl(grid)).madeChanges() shouldBe true

            println(grid)

            assertSoftly {
                withClue("possibles should be deleted from other cells") {
                    grid.cells[2].possibles shouldContainExactly setOf(2, 4)
                    grid.cells[3].possibles shouldContainExactly setOf(2, 4)
                }
            }
        }
    })
