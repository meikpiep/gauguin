package org.piepmeyer.gauguin.difficulty.human.strategy

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder

class YWingTest :
    FunSpec({

        test("Y wing gets found in small grid") {
            var allSinglePossibles = setOf(intArrayOf(1), intArrayOf(2), intArrayOf(3))

            val (grid, cageToPossibles) =
                GridBuilder(3, 3)
                    .addCageSingle(1, setOf(intArrayOf(1), intArrayOf(2)))
                    .addCageSingle(1, allSinglePossibles)
                    .addCageSingle(1, setOf(intArrayOf(1), intArrayOf(3)))
                    .addCageSingle(1, allSinglePossibles)
                    .addCageSingle(1, allSinglePossibles)
                    .addCageSingle(1, allSinglePossibles)
                    .addCageSingle(1, setOf(intArrayOf(2), intArrayOf(3)))
                    .addCageSingle(1, allSinglePossibles)
                    .addCageSingle(1, allSinglePossibles)
                    .createGridAndCageToPossibles()

            println(grid)

            val cacheWithPossibles = HumanSolverCacheWithFixedPossibles(grid, cageToPossibles)

            val solver = YWing()

            solver.fillCells(grid, cacheWithPossibles).madeChanges() shouldBe true
            solver.fillCells(grid, cacheWithPossibles).madeChanges() shouldBe false

            println(grid)

            assertSoftly {
                withClue("possible 3 of cell 8 should be erased") {
                    grid.cells[8].possibles shouldContainExactly setOf(1, 2)
                }
            }
        }
    })
